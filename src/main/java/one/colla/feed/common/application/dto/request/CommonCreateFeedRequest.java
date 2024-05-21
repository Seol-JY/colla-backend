package one.colla.feed.common.application.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CommonCreateFeedRequest<T extends CreateFeedDetails>(
	@NotBlank(message = "제목을 포함해주세요.")
	@Size(max = 50, message = "제목은 50자 이하여야 합니다")
	String title,

	@Valid
	List<FileDto> images,

	@Valid
	List<FileDto> attachments,

	@Valid
	T details
) {
	public record FileDto(
		@NotBlank(message = "파일명을 포함해주세요.")
		@Size(max = 255, message = "파일명은 255자 이하여야 합니다.")
		String name,

		@NotBlank(message = "파일 경로를 포함해주세요.")
		@URL(message = "URL 형식이 아닙니다.")
		@Size(max = 2048, message = "파일 경로는 2048 이하여야 합니다.")
		String fileUrl,

		@NotNull(message = "파일 크기를 포함해주세요.")
		@Positive(message = "파일 크기는 양수여야 합니다.")
		Long size
	) {
	}
}
