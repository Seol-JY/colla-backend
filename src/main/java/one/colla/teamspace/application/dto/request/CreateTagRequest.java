package one.colla.teamspace.application.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CreateTagRequest(
	@NotBlank(message = "태그를 입력해주세요.")
	@Length(min = 2, max = 15, message = "태그 길이는 2자 이상 15자 이하여야 합니다.")
	String tagName
) {
}
