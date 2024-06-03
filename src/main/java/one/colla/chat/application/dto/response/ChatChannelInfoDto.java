package one.colla.chat.application.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Builder;
import one.colla.chat.domain.ChatChannel;

@Builder
public record ChatChannelInfoDto(
	Long id,
	String name,
	String lastChatMessage,
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime lastChatCreatedAt,
	int unreadMessageCount
) {
	public static ChatChannelInfoDto of(ChatChannel chatChannel, String lastChatMessage,
		LocalDateTime lastChatCreatedAt, int unreadMessageCount) {
		return ChatChannelInfoDto.builder()
			.id(chatChannel.getId())
			.name(chatChannel.getChatChannelName().getValue())
			.lastChatMessage(lastChatMessage)
			.lastChatCreatedAt(lastChatCreatedAt)
			.unreadMessageCount(unreadMessageCount)
			.build();
	}
}
