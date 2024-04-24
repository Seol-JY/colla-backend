package one.colla.teamspace.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.application.dto.request.CreateTeamspaceRequest;
import one.colla.teamspace.application.dto.response.CreateTeamspaceResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces")
public class TeamspaceController {
	private final TeamspaceService teamspaceService;

	@PostMapping()
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CreateTeamspaceResponse>> createTeamspace(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid CreateTeamspaceRequest request) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(teamspaceService.create(userDetails, request))
		);
	}
}
