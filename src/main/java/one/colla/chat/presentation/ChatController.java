package one.colla.chat.presentation;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import one.colla.chat.application.ChatChannelService;
import one.colla.chat.application.dto.request.CreateChatChannelRequest;
import one.colla.chat.application.dto.request.UpdateChatChannelNameRequest;
import one.colla.chat.application.dto.response.ChatChannelMessagesResponse;
import one.colla.chat.application.dto.response.ChatChannelsResponse;
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

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ChatChannelsResponse>> getChatChannels(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccessResponse(chatChannelService.getChatChannels(userDetails, teamspaceId)));
	}

	@PatchMapping("/name")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> updateChatChannelName(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@RequestBody @Valid final UpdateChatChannelNameRequest request) {

		chatChannelService.updateChatChannelName(userDetails, teamspaceId, request);
		return ResponseEntity.ok().body(ApiResponse.createSuccessResponse(Map.of()));
	}

	@GetMapping("/{chatChannelId}/messages")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ChatChannelMessagesResponse>> getChatChannelMessages(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long teamspaceId,
		@PathVariable final Long chatChannelId,
		@RequestParam(value = "before", required = false) final Long beforeChatMessageId,
		@RequestParam(value = "limit", defaultValue = "50") @Valid @Positive final int limit) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccessResponse(
				chatChannelService.getChatChanelMessages(
					userDetails, teamspaceId, chatChannelId, beforeChatMessageId, limit)));
	}

}


