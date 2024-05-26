package one.colla.feed.common.application.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import one.colla.feed.common.domain.Comment;
import one.colla.user.domain.User;

@Builder
public record CommentDto(
	CommentAuthorDto author,
	String content,
	LocalDateTime createdAt
) {
	public static CommentDto from(Comment comment) {
		CommentAuthorDto commentAuthorDto = CommentAuthorDto.from(comment.getUser());
		return new CommentDto(commentAuthorDto, comment.getContent(), comment.getCreatedAt());
	}

	private record CommentAuthorDto(
		Long id,
		String profileImageUrl,
		String username
	) {
		public static CommentAuthorDto from(User user) {
			return new CommentAuthorDto(
				user.getId(),
				user.getProfileImageUrlValue(),
				user.getUsernameValue()
			);
		}
	}
}
