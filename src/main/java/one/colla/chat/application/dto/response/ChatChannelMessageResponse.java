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
public record ChatChannelMessageResponse(
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
	public static ChatChannelMessageResponse of(ChatChannelMessage chatChannelMessage,
		ChatChannelMessageAuthorDto author, List<ChatChannelMessageAttachmentDto> attachments) {

		String content = null;

		if (chatChannelMessage.getContent() != null) {
			content = chatChannelMessage.getContent().getValue();
		}
		return ChatChannelMessageResponse.builder()
			.id(chatChannelMessage.getId())
			.type(chatChannelMessage.getChatType())
			.chatChannelId(chatChannelMessage.getChatChannel().getId())
			.author(author)
			.content(content)
			.attachments(attachments)
			.createdAt(chatChannelMessage.getCreatedAt())
			.build();
	}
}
