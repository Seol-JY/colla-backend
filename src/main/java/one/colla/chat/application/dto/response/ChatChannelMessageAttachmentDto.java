package one.colla.chat.application.dto.response;

import lombok.Builder;
import one.colla.chat.domain.ChatChannelMessageAttachment;

@Builder
public record ChatChannelMessageAttachmentDto(
	Long id,
	String filename,
	Long size,
	String url,
	String attachType
) {
	public static ChatChannelMessageAttachmentDto from(ChatChannelMessageAttachment chatChannelMessageAttachment) {
		return ChatChannelMessageAttachmentDto.builder()
			.id(chatChannelMessageAttachment.getAttachment().getId())
			.filename(chatChannelMessageAttachment.getAttachment().getAttachmentName().getValue())
			.size(chatChannelMessageAttachment.getAttachment().getSize())
			.url(chatChannelMessageAttachment.getAttachment().getFileUrl().getValue())
			.attachType(chatChannelMessageAttachment.getAttachment().getAttachType())
			.build();
	}
}
