package one.colla.teamspace.presentation;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.application.dto.request.CreateTeamspaceRequest;
import one.colla.teamspace.application.dto.response.CreateTeamspaceResponse;
import one.colla.teamspace.application.dto.response.TeamspaceInfoResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces")
public class TeamspaceController {
	private final TeamspaceService teamspaceService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CreateTeamspaceResponse>> createTeamspace(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid CreateTeamspaceRequest request) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(teamspaceService.create(userDetails, request))
		);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<TeamspaceInfoResponse>> readTeamspaceInfo(
		@AuthenticationPrincipal Optional<CustomUserDetails> userDetails,
		@RequestParam(required = true) String inviteCode
	) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(teamspaceService.readInfoByCode(userDetails, inviteCode))
		);
	}
}
