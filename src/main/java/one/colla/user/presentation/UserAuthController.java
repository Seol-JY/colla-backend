package one.colla.user.presentation;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.util.CookieUtil;
import one.colla.user.application.UserAuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserAuthController {
	private final UserAuthService userAuthService;
	private final CookieUtil cookieUtil;

	@GetMapping("/users/logout")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> signOut(
		@RequestHeader("Authorization") String authHeader,
		@CookieValue(value = "refreshToken", required = false) String refreshToken,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		String accessToken = authHeader.split(" ")[1];
		userAuthService.logOut(user.getUserId(), accessToken, refreshToken);
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookieUtil.deleteCookie("refreshToken").toString())
			.body(ApiResponse.createSuccessResponse(Map.of()));
	}
}
