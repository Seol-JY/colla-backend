package one.colla.chat.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Builder;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatType;

@Builder
public record ChatChannelMessageInfoDto(
	Long id,
	ChatType type,
	Long chatChannelId,
	ChatChannelMessageAuthorDto author,
	String content,
	List<ChatChannelMessageAttachmentDto> attachments,
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime createdAt
) {
	public static ChatChannelMessageInfoDto of(ChatChannelMessage chatChannelMessage,
		ChatChannelMessageAuthorDto author, List<ChatChannelMessageAttachmentDto> attachments) {
		return ChatChannelMessageInfoDto.builder()
			.id(chatChannelMessage.getId())
			.type(chatChannelMessage.getChatType())
			.chatChannelId(chatChannelMessage.getChatChannel().getId())
			.author(author)
			.content(chatChannelMessage.getContent().getValue())
			.attachments(attachments)
			.createdAt(chatChannelMessage.getCreatedAt())
			.build();
	}
}
