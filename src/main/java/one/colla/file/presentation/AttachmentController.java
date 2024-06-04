package one.colla.file.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.file.application.AttachmentService;
import one.colla.file.application.dto.response.StorageResponse;
import one.colla.file.domain.AttachmentType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces/{teamspaceId}/attachments")
public class AttachmentController {
	private final AttachmentService attachmentService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<StorageResponse>> getChatChannels(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@RequestParam(value = "type", required = false) final AttachmentType type,
		@RequestParam(value = "attach-type", required = false) final String attachType,
		@RequestParam(value = "username", required = false) final String username) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccessResponse(
				attachmentService.getAttachments(userDetails, teamspaceId, type, attachType, username)));
	}
}
