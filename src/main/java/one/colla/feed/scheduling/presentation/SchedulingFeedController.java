package one.colla.feed.scheduling.presentation;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.scheduling.application.SchedulingFeedService;
import one.colla.feed.scheduling.application.dto.request.CreateSchedulingFeedDetails;
import one.colla.feed.scheduling.application.dto.request.PutSchedulingAvailabilitiesRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces/{teamspaceId}/feeds/scheduling")
public class SchedulingFeedController {
	private final SchedulingFeedService schedulingFeedService;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> postSchedulingFeed(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@RequestBody @Valid final CommonCreateFeedRequest<CreateSchedulingFeedDetails> createSchedulingFeedRequest
	) {
		schedulingFeedService.create(userDetails, teamspaceId, createSchedulingFeedRequest);

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}

	@PutMapping("/{feedId}/availabilities")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> putSchedulingAvailability(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long feedId,
		@RequestBody @Valid final PutSchedulingAvailabilitiesRequest request
	) {
		schedulingFeedService.updateSchedulingAvailability(userDetails, teamspaceId, feedId, request);

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}
}
