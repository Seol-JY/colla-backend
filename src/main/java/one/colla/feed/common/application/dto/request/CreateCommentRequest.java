package one.colla.feed.common.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
	@NotBlank(message = "댓글 내용을 포함해주세요.")
	@Size(max = 250, message = "댓글 내용은 250자 이하여야 합니다")
	String content
) {
}
