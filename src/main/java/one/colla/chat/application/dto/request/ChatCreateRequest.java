package one.colla.chat.application.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import one.colla.chat.domain.ChatType;
import one.colla.global.config.json.NoStrip;

public record ChatCreateRequest(
	@NotNull(message = "채팅 타입을 입력해주세요.")
	ChatType chatType,

	@NoStrip
	@Size(max = 1024, message = "채팅 글자 수는 1024자 이하여야 합니다.")
	String content,

	List<FileDto> images,

	List<FileDto> attachments
) {

	@AssertTrue(message = "IMAGE 타입의 경우 이미지 파일이 있어야 합니다.")
	public boolean isImagesValid() {
		if (chatType == ChatType.IMAGE) {
			return images != null && !images.isEmpty();
		}
		return true;
	}

	@AssertTrue(message = "FILE 타입의 경우 첨부 파일이 있어야 합니다.")
	public boolean isAttachmentsValid() {
		if (chatType == ChatType.FILE) {
			return attachments != null && !attachments.isEmpty();
		}
		return true;
	}

	public record FileDto(
		@NotBlank(message = "파일명을 포함해주세요.")
		@Size(max = 255, message = "파일명은 255자 이하여야 합니다.")
		String name,

		@NotBlank(message = "파일 경로를 포함해주세요.")
		@URL(message = "URL 형식이 아닙니다.")
		@Size(max = 2048, message = "파일 경로는 2048자 이하여야 합니다.")
		String fileUrl,

		@NotNull(message = "파일 크기를 포함해주세요.")
		@Positive(message = "파일 크기는 양수여야 합니다.")
		Long size
	) {
	}
}
