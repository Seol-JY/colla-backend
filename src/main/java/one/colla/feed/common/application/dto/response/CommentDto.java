package one.colla.feed.common.application.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommentDto(
	CommentAuthorDto author,
	String content,
	LocalDateTime createdAt
) {

	public record CommentAuthorDto(
		Long id,
		String profileImageUrl,
		String username
	) {
	}
}
