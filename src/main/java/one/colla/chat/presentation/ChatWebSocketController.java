package one.colla.chat.presentation;

import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.chat.application.ChatWebSocketService;
import one.colla.chat.application.dto.request.ChatCreateRequest;
import one.colla.chat.application.dto.response.ChatChannelMessageResponse;
import one.colla.chat.application.dto.response.ChatChannelStatusResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

	private final SimpMessagingTemplate template;
	private final ChatWebSocketService chatWebSocketService;

	@MessageMapping("/teamspaces/{teamspaceId}/chat-channels/{chatChannelId}/messages")
	public void sendMessage(
		@Payload @Valid ChatCreateRequest request,
		@DestinationVariable Long teamspaceId,
		@DestinationVariable Long chatChannelId,
		StompHeaderAccessor headerAccessor) {

		Long userId = getUserIdFromHeaderAccessor(headerAccessor);

		ChatChannelMessageResponse chatChannelMessageResponse = chatWebSocketService.processMessage(
			request, userId, teamspaceId, chatChannelId);

		template.convertAndSend("/topic/teamspaces/" + teamspaceId + "/chat-channels/" + chatChannelId + "/messages",
			chatChannelMessageResponse);
		log.info("채팅 메시지 전송 - 사용자 Id: {}, 팀스페이스 Id: {}, 채널 Id: {}", userId, teamspaceId, chatChannelId);

		template.convertAndSend("/topic/teamspaces/" + teamspaceId + "/receive-message", Map.of());
		log.info("팀스페이스 전역 채팅 메시지 수신 - 팀스페이스 Id: {}", teamspaceId);

	}

	@MessageMapping("/teamspaces/{teamspaceId}/users/{userId}/chat-channels/status")
	public void getChatChannelsStatus(
		@DestinationVariable Long teamspaceId,
		@DestinationVariable Long userId,
		StompHeaderAccessor headerAccessor) {

		Long connectedUserId = getUserIdFromHeaderAccessor(headerAccessor);

		ChatChannelStatusResponse response = chatWebSocketService.getChatChannelsStatus(connectedUserId, teamspaceId);
		template.convertAndSend(
			"/topic/teamspaces/" + teamspaceId + "/users/" + userId + "/chat-channels/status",
			response);
		log.info("채팅 채널 상태 조회 - 사용자 Id: {}, 팀스페이스 Id: {}", userId, teamspaceId);
	}

	@MessageMapping("/teamspaces/{teamspaceId}/chat-channels/{chatChannelId}/messages/{messageId}/read")
	public void markMessageAsRead(
		@DestinationVariable Long teamspaceId,
		@DestinationVariable Long chatChannelId,
		@DestinationVariable Long messageId,
		StompHeaderAccessor headerAccessor) {

		Long userId = getUserIdFromHeaderAccessor(headerAccessor);

		chatWebSocketService.markMessageAsRead(userId, teamspaceId, chatChannelId, messageId);

		log.info("메시지 읽음 상태 업데이트 - 사용자 Id: {}, 팀스페이스 Id: {}, 채널 Id: {}, 메시지 Id: {}", userId, teamspaceId, chatChannelId,
			messageId);
	}

	private Long getUserIdFromHeaderAccessor(StompHeaderAccessor headerAccessor) {
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		if (sessionAttributes == null) {
			throw new IllegalArgumentException("Session attributes are required");
		}
		return (Long)sessionAttributes.get("userId");
	}

}
