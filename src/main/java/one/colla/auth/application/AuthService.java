package one.colla.auth.application;

import static one.colla.global.exception.ExceptionCode.*;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import one.colla.auth.application.dto.JwtPair;
import one.colla.auth.application.dto.request.DuplicationCheckRequest;
import one.colla.auth.application.dto.request.LoginRequest;
import one.colla.auth.application.dto.request.VerificationCheckRequest;
import one.colla.auth.application.dto.request.VerifyMailSendRequest;
import one.colla.common.util.RandomCodeGenerator;
import one.colla.global.exception.CommonException;
import one.colla.infra.mail.VerifyCodeMailSendEvent;
import one.colla.infra.redis.verify.VerifyCode;
import one.colla.infra.redis.verify.VerifyCodeService;
import one.colla.user.application.UserService;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
	private static final int VERIFY_CODE_LENGTH = 7;
	private static final long REGISTER_VERIFY_CODE_EXPIRY_TIME = 1_200; // 20m (20 * 60)

	private final UserService userService;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RandomCodeGenerator randomCodeGenerator;
	private final VerifyCodeService verifyCodeService;
	private final ApplicationEventPublisher publisher;

	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id) {
		return userService.getUserById(id);
	}

	@Transactional(readOnly = true)
	public Pair<Long, JwtPair> login(LoginRequest dto) {
		User user = userRepository.findByEmail(dto.email())
			.orElseThrow(() -> new CommonException(INVALID_USERNAME_OR_PASSWORD));

		if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
			throw new CommonException(INVALID_USERNAME_OR_PASSWORD);
		}

		JwtPair jwtPair = jwtService.createToken(user);
		return Pair.of(user.getId(), jwtPair);
	}

	@Transactional(readOnly = true)
	public void checkDuplication(DuplicationCheckRequest dto) {
		checkDuplicationEmail(dto.email());
	}

	@Transactional(readOnly = true)
	public void checkVerification(VerificationCheckRequest dto) {
		if (!isVerificationCode(dto.email(), dto.verifyCode())) {
			throw new CommonException(INVALID_VERIFY_TOKEN);
		}
	}

	@Transactional
	public void sendVerifyMail(VerifyMailSendRequest dto) {
		String generated = randomCodeGenerator.generateRandomString(VERIFY_CODE_LENGTH);

		verifyCodeService.save(
			VerifyCode.of(dto.email(), generated, REGISTER_VERIFY_CODE_EXPIRY_TIME)
		);

		publisher.publishEvent(new VerifyCodeMailSendEvent(dto.email(), generated));
	}

	// NOTE: DO NOT Insert transaction annotations
	public Pair<Long, JwtPair> refresh(String refreshToken) {
		return jwtService.refresh(refreshToken);
	}

	private void checkDuplicationEmail(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isPresent()) {
			throw new CommonException(DUPLICATED_USER_EMAIL);
		}
	}

	private boolean isVerificationCode(String email, String verifyCode) {
		VerifyCode findVerifyCode = verifyCodeService.findByEmail(email)
			.orElseThrow(() -> new CommonException(INVALID_VERIFY_TOKEN));
		String getCode = findVerifyCode.getVerifyCode();

		return getCode.equals(verifyCode);
	}
}
