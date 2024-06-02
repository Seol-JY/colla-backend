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

		ChatChannelMessageResponse response = chatWebSocketService.processMessage(
			request, userId, teamspaceId, chatChannelId);
		template.convertAndSend("/topic/teamspaces/" + teamspaceId + "/chat-channels/" + chatChannelId + "/messages",
			response);
		log.info("채팅 메시지 전송 - 사용자 Id: {}, 팀스페이스 Id: {}, 채널 Id: {}", userId, teamspaceId, chatChannelId);
	}

	private Long getUserIdFromHeaderAccessor(StompHeaderAccessor headerAccessor) {
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		if (sessionAttributes == null) {
			throw new IllegalArgumentException("Session attributes are required");
		}
		return (Long)sessionAttributes.get("userId");
	}

}
