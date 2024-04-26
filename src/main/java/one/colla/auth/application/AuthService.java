package one.colla.auth.application;

import static one.colla.global.exception.ExceptionCode.*;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.auth.application.dto.JwtPair;
import one.colla.auth.application.dto.request.DuplicationCheckRequest;
import one.colla.auth.application.dto.request.LoginRequest;
import one.colla.auth.application.dto.request.RegisterRequest;
import one.colla.auth.application.dto.request.VerificationCheckRequest;
import one.colla.auth.application.dto.request.VerifyMailSendRequest;
import one.colla.common.util.RandomCodeGenerator;
import one.colla.global.exception.CommonException;
import one.colla.infra.mail.events.VerifyCodeSendMailEvent;
import one.colla.infra.redis.verify.VerifyCode;
import one.colla.infra.redis.verify.VerifyCodeService;
import one.colla.user.application.UserService;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;
import one.colla.user.domain.vo.Email;

@Service
@RequiredArgsConstructor
@Slf4j
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
		User user = userRepository.findByEmail(new Email(dto.email()))
			.orElseThrow(() -> new CommonException(INVALID_USERNAME_OR_PASSWORD));

		if (!passwordEncoder.matches(dto.password(), user.getPasswordValue())) {
			throw new CommonException(INVALID_USERNAME_OR_PASSWORD);
		}

		JwtPair jwtPair = jwtService.createToken(user);
		log.info("유저 로그인 - 유저 Id: {}", user.getId());
		return Pair.of(user.getId(), jwtPair);
	}

	@Transactional
	public void register(RegisterRequest dto) {
		isEmailDuplicated(dto.email());
		if (isMismatchedVerifyCode(dto.email(), dto.verifyCode())) {
			throw new CommonException(UNAUTHORIZED_OR_EXPIRED_VERIFY_TOKEN);
		}
		User user = User.createGeneralUser(dto.username(), passwordEncoder.encode(dto.password()), dto.email());
		userRepository.save(user);
		log.info("유저 회원가입 - 유저 Id: {}", user.getId());
	}

	@Transactional(readOnly = true)
	public void checkDuplication(DuplicationCheckRequest dto) {
		isEmailDuplicated(dto.email());
		log.info("이메일 중복 검사 - email: {}", dto.email());
	}

	@Transactional(readOnly = true)
	public void checkVerification(VerificationCheckRequest dto) {
		if (isMismatchedVerifyCode(dto.email(), dto.verifyCode())) {
			throw new CommonException(INVALID_VERIFY_TOKEN);
		}
		log.info("인증번호 검증 - email: {}", dto.email());
	}

	@Transactional
	public void sendVerifyMail(VerifyMailSendRequest dto) {
		String generated = randomCodeGenerator.generateRandomString(VERIFY_CODE_LENGTH);

		verifyCodeService.save(
			VerifyCode.of(dto.email(), generated, REGISTER_VERIFY_CODE_EXPIRY_TIME)
		);

		publisher.publishEvent(new VerifyCodeSendMailEvent(dto.email(), generated));
		log.info("인증번호 전송 - email: {}", dto.email());
	}

	// NOTE: DO NOT Insert transaction annotations
	public Pair<Long, JwtPair> refresh(String refreshToken) {
		return jwtService.refresh(refreshToken);
	}

	private void isEmailDuplicated(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(new Email(email));
		if (optionalUser.isPresent()) {
			throw new CommonException(DUPLICATED_USER_EMAIL);
		}
	}

	private boolean isMismatchedVerifyCode(String email, String verifyCode) {
		Optional<VerifyCode> findVerifyCode = verifyCodeService.findByEmail(email);
		if (findVerifyCode.isEmpty()) {
			return true;
		}
		String getCode = findVerifyCode.get().getVerifyCode();
		return !getCode.equals(verifyCode);
	}
}
