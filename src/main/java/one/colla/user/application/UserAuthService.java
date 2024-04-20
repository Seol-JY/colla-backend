package one.colla.user.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import one.colla.auth.application.JwtService;

@Service
@RequiredArgsConstructor
public class UserAuthService {
	private final JwtService jwtService;

	@Transactional
	public void logOut(Long userId, String authHeader, String refreshToken) {
		jwtService.removeAccessTokenAndRefreshToken(userId, authHeader, refreshToken);
	}
}
