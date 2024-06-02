package one.colla.feed.collect.application.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import jakarta.annotation.Nullable;
import one.colla.feed.collect.domain.CollectFeedResponse;
import one.colla.feed.collect.domain.CollectFeedStatus;
import one.colla.teamspace.domain.Tag;
import one.colla.user.domain.User;

public record ReadCollectFeedResponseResponse(
	CollectResponseAuthorDto author,
	String title,
	CollectFeedStatus status,
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime updatedAt,
	String content
) {
	public static ReadCollectFeedResponseResponse of(
		CollectResponseAuthorDto author,
		CollectFeedResponse response
	) {
		return new ReadCollectFeedResponseResponse(
			author,
			response.getTitle(),
			response.getCollectFeedStatus(),
			response.getUpdatedAt(),
			response.getContent());
	}

	public record CollectResponseAuthorDto(
		Long id,
		String profileImageUrl,
		String username,
		TagDto tag
	) {
		public static CollectResponseAuthorDto of(User user, TagDto tagDto) {
			return new CollectResponseAuthorDto(
				user.getId(),
				user.getProfileImageUrlValue(),
				user.getUsernameValue(),
				tagDto
			);
		}
	}

	public record TagDto(
		Long id,
		String name
	) {
		public static TagDto from(@Nullable Tag tag) {
			return tag != null ? new TagDto(tag.getId(), tag.getTagNameValue()) : null;
		}
	}
}
