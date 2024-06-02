package one.colla.feed.collect.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import one.colla.feed.collect.domain.CollectFeedResponse;
import one.colla.feed.collect.domain.CollectFeedStatus;
import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.user.domain.User;

public record ReadCollectFeedDetails(
	String content,
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime dueAt,
	boolean isClosed,
	List<ReadCollectFeedResponse> responses
) implements ReadFeedDetails {
	public static ReadCollectFeedDetails of(
		String content,
		LocalDateTime dueAt,
		boolean isClosed,
		List<ReadCollectFeedResponse> responses
	) {
		return new ReadCollectFeedDetails(content, dueAt, isClosed, responses);
	}

	public record ReadCollectFeedResponse(
		String title,
		CollectFeedStatus status,
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime updatedAt,
		CollectFeedResponseAuthor author
	) {
		public static ReadCollectFeedResponse of(CollectFeedResponse response, CollectFeedResponseAuthor author) {
			return new ReadCollectFeedResponse(
				response.getTitle(),
				response.getCollectFeedStatus(),
				response.getUpdatedAt(),
				author
			);
		}
	}

	public record CollectFeedResponseAuthor(
		Long id,
		String profileImageUrl,
		String username
	) {
		public static CollectFeedResponseAuthor from(User user) {
			return new CollectFeedResponseAuthor(user.getId(), user.getProfileImageUrlValue(), user.getUsernameValue());
		}
	}
}
