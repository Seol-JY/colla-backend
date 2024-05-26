package one.colla.chat.application.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import one.colla.chat.domain.ChatChannel;

@Builder
public record ChatChannelInfoDto(
	Long id,
	String name,
	String lastChatMessage,
	String lastChatCreatedAt
) {
	public static ChatChannelInfoDto of(ChatChannel chatChannel, String lastChatMessage,
		LocalDateTime lastChatCreatedAt) {
		return ChatChannelInfoDto.builder()
			.id(chatChannel.getId())
			.name(chatChannel.getChatChannelName().getValue())
			.lastChatMessage(lastChatMessage)
			.lastChatCreatedAt(lastChatCreatedAt != null ? String.valueOf(lastChatCreatedAt) : null)
			.build();
	}
}
