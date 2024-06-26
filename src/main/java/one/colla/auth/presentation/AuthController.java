package one.colla.auth.presentation;

import java.time.Duration;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.auth.application.AuthService;
import one.colla.auth.application.OAuthService;
import one.colla.auth.application.dto.JwtPair;
import one.colla.auth.application.dto.request.LoginRequest;
import one.colla.auth.application.dto.request.OAuthLoginRequest;
import one.colla.auth.application.dto.request.RegisterRequest;
import one.colla.auth.application.dto.request.VerificationCheckRequest;
import one.colla.auth.application.dto.request.VerifyMailSendRequest;
import one.colla.auth.application.dto.response.LoginResponse;
import one.colla.auth.application.dto.response.OauthLoginUrlResponse;
import one.colla.auth.config.OAuthProperties;
import one.colla.auth.config.OAuthPropertyFactory;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.util.CookieUtil;
import one.colla.common.util.oauth.OAuthUriGenerator;
import one.colla.user.application.UserService;
import one.colla.user.domain.OauthProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private static final int REFRESH_TOKEN_EXPIRES_IN_DAYS = 7;

	private final AuthService authService;
	private final CookieUtil cookieUtil;
	private final OAuthPropertyFactory oAuthPropertyFactory;
	private final OAuthUriGenerator oAuthUriGenerator;
	private final OAuthService oAuthService;
	private final UserService userService;

	@GetMapping("/oauth/{provider}/login")
	public ResponseEntity<ApiResponse<OauthLoginUrlResponse>> getOAuthUrl(
		@PathVariable(value = "provider") final OauthProvider oauthProvider) {
		OAuthProperties oAuthProperties = oAuthPropertyFactory.createOAuthProperty(oauthProvider);
		OauthLoginUrlResponse oauthUrl = oAuthUriGenerator.generate(oAuthProperties);
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccessResponse(oauthUrl));
	}

	@PostMapping("/oauth/{provider}/code")
	public ResponseEntity<ApiResponse<LoginResponse>> oauthRegisterOrLogin(
		@PathVariable("provider") final OauthProvider oauthProvider, @RequestBody @Valid OAuthLoginRequest request) {
		return createAuthResponse(oAuthService.createToken(request, oauthProvider));
	}

	@PostMapping("/login")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<LoginResponse>> signIn(@RequestBody @Valid LoginRequest request) {
		return createAuthResponse(authService.login(request));
	}

	@PostMapping("/register")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Object>> signup(@RequestBody @Valid RegisterRequest request) {
		authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.createSuccessResponse(Map.of()));
	}

	@PostMapping("/mail/send")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Object>> sendVerifyMail(@RequestBody @Valid VerifyMailSendRequest request) {
		authService.sendVerifyMail(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.createSuccessResponse(Map.of()));
	}

	@GetMapping("/mail/duplication")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Object>> checkDuplication(
		@RequestParam(value = "email", required = true) String email) {
		authService.checkDuplication(email);
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccessResponse(Map.of()));
	}

	@PostMapping("/mail/verification")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Object>> checkVerification(@RequestBody @Valid VerificationCheckRequest request) {
		authService.checkVerification(request);
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccessResponse(Map.of()));
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
			Duration.ofDays(REFRESH_TOKEN_EXPIRES_IN_DAYS).toSeconds());

		boolean hasTeam = userService.hasTeam(userId);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.body(ApiResponse.createSuccessResponse(LoginResponse.of(accessToken, userId, hasTeam)));
	}
}
