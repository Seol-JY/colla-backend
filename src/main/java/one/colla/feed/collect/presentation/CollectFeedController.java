package one.colla.feed.collect.presentation;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.collect.application.CollectFeedService;
import one.colla.feed.collect.application.dto.request.CreateCollectFeedDetails;
import one.colla.feed.collect.application.dto.request.UpdateCollectFeedResponseRequest;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces/{teamspaceId}/feeds/collect")
public class CollectFeedController {
	private final CollectFeedService collectFeedService;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> postCollectFeed(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@RequestBody @Valid final CommonCreateFeedRequest<CreateCollectFeedDetails> createCollectFeedRequest
	) {
		collectFeedService.create(userDetails, teamspaceId, createCollectFeedRequest);

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}

	@GetMapping("/{feedId}/responses/users/{userId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> getCollectFeedResponse(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long feedId,
		@PathVariable final Long userId
	) {

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(
				collectFeedService.readResponse(userDetails, teamspaceId, feedId, userId)
			)
		);
	}

	@PatchMapping("/{feedId}/responses")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> patchCollectFeedResponse(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long feedId,
		@RequestBody @Valid final UpdateCollectFeedResponseRequest request
	) {
		collectFeedService.updateResponse(userDetails, teamspaceId, feedId, request);

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}
}
