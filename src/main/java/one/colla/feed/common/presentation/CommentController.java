package one.colla.feed.common.presentation;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import one.colla.feed.common.application.CommentService;
import one.colla.feed.common.application.dto.request.CreateCommentRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces/{teamspaceId}/feeds/{feedId}/comments")
public class CommentController {
	private final CommentService commentService;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> createComment(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long feedId,
		@RequestBody @Valid final CreateCommentRequest request
	) {
		commentService.create(userDetails, teamspaceId, feedId, request);

		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(
				Map.of()
			)
		);
	}

	@PatchMapping("/{commentId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> patchComment(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long feedId,
		@PathVariable final Long commentId
	) {

		// TODO: 댓글 수정 구현 필요
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}

	@DeleteMapping("/{commentId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Object>> deleteComment(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long feedId,
		@PathVariable final Long commentId
	) {

		// TODO: 댓글 삭제 구현 필요
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(Map.of())
		);
	}
}
