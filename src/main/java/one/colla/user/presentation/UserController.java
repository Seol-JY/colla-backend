package one.colla.user.presentation;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.user.application.UserService;
import one.colla.user.application.dto.request.LastSeenUpdateRequest;
import one.colla.user.application.dto.response.UserStatusResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
	private final UserService userService;

	@GetMapping("/status")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<UserStatusResponse>> getUserStatus(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(userService.getUserStatus(userDetails))
		);
	}

	@PostMapping("/last-seen")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> updateLastSeenTeamspace(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid final LastSeenUpdateRequest request) {
		userService.updateLastSeenTeamspace(userDetails, request);
		return ResponseEntity.ok().body(ApiResponse.createSuccessResponse(Map.of())
		);
	}

}
