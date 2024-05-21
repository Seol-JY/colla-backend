package one.colla.feed.common.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import one.colla.feed.common.domain.FeedType;

@Builder
public record CommonReadFeedResponse<T extends ReadFeedDetails>(
	FeedType feedType,
	Long feedId,
	FeedAuthorDto author,
	String title,
	LocalDateTime createdAt,
	T details,
	List<CommentDto> comments,
	List<FileDto> images,
	List<FileDto> attachments
) {
	public record FeedAuthorDto(
		Long id,
		String profileImageUrl,
		String username,
		TagDto tag
	) {
	}

	public record TagDto(
		Long id,
		String name
	) {
	}

	public record FileDto(
		Long id,
		String name,
		String fileUrl,
		Long size
	) {
	}
}
