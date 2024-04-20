package one.colla.auth.application;

import static one.colla.global.exception.ExceptionCode.*;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import one.colla.auth.application.dto.JwtPair;
import one.colla.auth.application.dto.request.LoginRequest;
import one.colla.global.exception.CommonException;
import one.colla.user.application.UserService;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserService userService;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

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

	// NOTE: DO NOT Insert transaction annotations
	public Pair<Long, JwtPair> refresh(String refreshToken) {
		return jwtService.refresh(refreshToken);
	}
}
