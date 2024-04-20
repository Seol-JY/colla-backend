package one.colla.auth.presentation;

import java.time.Duration;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.auth.application.AuthService;
import one.colla.auth.application.dto.JwtPair;
import one.colla.auth.application.dto.request.LoginRequest;
import one.colla.auth.application.dto.response.LoginResponse;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.util.CookieUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final AuthService authService;
	private final CookieUtil cookieUtil;

	@PostMapping("/login")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<LoginResponse>> signIn(@RequestBody @Valid LoginRequest request) {
		return createAuthResponse(authService.login(request));
	}

	@GetMapping("/refresh")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<LoginResponse>> refresh(@CookieValue("refreshToken") String refreshToken) {
		return createAuthResponse(authService.refresh(refreshToken));
	}

	private ResponseEntity<ApiResponse<LoginResponse>> createAuthResponse(Pair<Long, JwtPair> pair) {
		Long userId = pair.getLeft();
		String accessToken = pair.getRight().accessToken();
		String refreshToken = pair.getRight().refreshToken();

		ResponseCookie cookie = cookieUtil.createCookie("refreshToken", refreshToken,
			Duration.ofDays(7).toSeconds());
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.body(ApiResponse.createSuccessResponse(LoginResponse.of(accessToken, userId)));
	}
}
