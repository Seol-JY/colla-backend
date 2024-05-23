package one.colla.chat.presentation;

import org.springframework.http.HttpStatus;
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
import one.colla.chat.application.ChatChannelService;
import one.colla.chat.application.dto.request.CreateChatChannelRequest;
import one.colla.chat.application.dto.response.CreateChatChannelResponse;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teamspaces/{teamspaceId}/chat-channels")
public class ChatController {

	private final ChatChannelService chatChannelService;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CreateChatChannelResponse>> createChatChannel(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@RequestBody @Valid final CreateChatChannelRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccessResponse(
			chatChannelService.createChatChannel(userDetails, teamspaceId, request)));
	}

}


