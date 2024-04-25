package one.colla.teamspace.presentation;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import one.colla.teamspace.application.dto.request.ParticipateRequest;
import one.colla.teamspace.application.dto.request.SendMailInviteCodeRequest;
import one.colla.teamspace.application.dto.response.CreateTeamspaceResponse;
import one.colla.teamspace.application.dto.response.InviteCodeResponse;
import one.colla.teamspace.application.dto.response.TeamspaceInfoResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces")
public class TeamspaceController {
	private final TeamspaceService teamspaceService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CreateTeamspaceResponse>> createTeamspace(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final CreateTeamspaceRequest request) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(teamspaceService.create(userDetails, request))
		);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<TeamspaceInfoResponse>> readTeamspaceInfo(
		@AuthenticationPrincipal final Optional<CustomUserDetails> userDetails,
		@RequestParam(required = true) final String inviteCode
	) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(teamspaceService.readInfoByCode(userDetails, inviteCode))
		);
	}

	@PostMapping("/{teamspaceId}/invitations")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<InviteCodeResponse>> getTeamspaceInviteCode(
		@PathVariable final Long teamspaceId
	) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(teamspaceService.getInviteCode(teamspaceId))
		);
	}

	@PostMapping("/{teamspaceId}/invitations/mails")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> sendTeamspaceInviteCode(
		@PathVariable final Long teamspaceId,
		@RequestBody @Valid final SendMailInviteCodeRequest request
	) {
		teamspaceService.sendInviteCode(teamspaceId, request);

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}

	@PostMapping("/{teamspaceId}/participants")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> participateTeamspace(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@RequestBody @Valid final ParticipateRequest request
	) {
		teamspaceService.participate(userDetails, teamspaceId, request);

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}

}
