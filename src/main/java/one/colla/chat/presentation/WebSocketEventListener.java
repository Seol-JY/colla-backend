package one.colla.chat.presentation;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketEventListener {

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {
		log.info("소켓 연결 시작- 사용자 ID: {}", event.getMessage().getHeaders().get("simpSessionAttributes"));

	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		log.info("소켓 연결 해제 - 사용자 ID: {}", event.getMessage().getHeaders().get("simpSessionAttributes"));
	}
}
