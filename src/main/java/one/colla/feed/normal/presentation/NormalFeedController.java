package one.colla.feed.normal.presentation;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.normal.application.NormalFeedService;
import one.colla.feed.normal.application.dto.request.CreateNormalFeedDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces/{teamspaceId}/feeds/normal")
public class NormalFeedController {
	private final NormalFeedService normalFeedService;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> postNormalFeed(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@RequestBody @Valid final CommonCreateFeedRequest<CreateNormalFeedDetails> createNormalFeedRequest
	) {
		normalFeedService.create(userDetails, teamspaceId, createNormalFeedRequest);

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}
}
