package one.colla.feed.common.presentation;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.application.FeedService;
import one.colla.feed.common.application.dto.response.CommonReadFeedListResponse;
import one.colla.feed.common.application.dto.response.CommonReadFeedResponse;
import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.FeedType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces/{teamspaceId}/feeds")
public class FeedController {
	private final FeedService feedService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CommonReadFeedListResponse>> getFeeds(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@RequestParam(value = "after", required = false) final Long afterFeedId,
		@RequestParam(value = "type", required = false) final FeedType feedType,
		@RequestParam(value = "limit", defaultValue = "5") @Valid @Positive final int limit
	) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(
				feedService.readFeeds(userDetails, teamspaceId, afterFeedId, feedType, limit)
			)
		);
	}

	@GetMapping("/{feedId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CommonReadFeedResponse<ReadFeedDetails>>> getFeed(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long feedId
	) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(
				feedService.readFeed(userDetails, teamspaceId, feedId)
			)
		);
	}

	@DeleteMapping("/{feedId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> deleteFeed(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long feedId
	) {
		feedService.delete(userDetails, teamspaceId, feedId);
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}
}
