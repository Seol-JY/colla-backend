package one.colla.feed.common.application.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Builder;
import one.colla.feed.common.domain.Comment;
import one.colla.user.domain.User;

@Builder
public record CommentDto(
	CommentAuthorDto author,
	String content,
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt
) {
	public static CommentDto from(Comment comment) {
		CommentAuthorDto commentAuthorDto = CommentAuthorDto.from(comment.getUser());
		return new CommentDto(commentAuthorDto, comment.getContent(), comment.getCreatedAt());
	}

	public record CommentAuthorDto(
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
