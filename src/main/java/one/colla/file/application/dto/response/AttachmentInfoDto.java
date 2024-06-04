package one.colla.file.application.dto.response;

import lombok.Builder;
import one.colla.file.domain.Attachment;
import one.colla.file.domain.AttachmentType;

@Builder
public record AttachmentInfoDto(
	Long id,
	String name,
	AttachmentType type,
	Long size,
	String attachType,
	String fileUrl,
	AttachmentAuthorDto author
) {
	public static AttachmentInfoDto of(Attachment attachment, AttachmentAuthorDto author) {
		return AttachmentInfoDto.builder()
			.id(attachment.getId())
			.name(attachment.getAttachmentNameValue())
			.type(attachment.getAttachmentType())
			.size(attachment.getSize())
			.attachType(attachment.getAttachType())
			.fileUrl(attachment.getFileUrlValue())
			.author(author)
			.build();
	}
}
