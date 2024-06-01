package one.colla.feed.common.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import jakarta.annotation.Nullable;
import lombok.Builder;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedType;
import one.colla.file.domain.Attachment;
import one.colla.teamspace.domain.Tag;
import one.colla.user.domain.User;

@Builder
public record CommonReadFeedResponse<T extends ReadFeedDetails>(
	FeedType feedType,
	Long feedId,
	FeedAuthorDto author,
	String title,
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime createdAt,
	T details,
	List<CommentDto> comments,
	List<FileDto> images,
	List<FileDto> attachments
) {
	public static <T extends ReadFeedDetails> CommonReadFeedResponse<T> of(
		FeedType feedType,
		Feed feed,
		FeedAuthorDto feedAuthorDto,
		T details,
		List<CommentDto> comments,
		List<FileDto> images,
		List<FileDto> attachments
	) {
		return new CommonReadFeedResponse<>(
			feedType,
			feed.getId(),
			feedAuthorDto,
			feed.getTitle(),
			feed.getCreatedAt(),
			details,
			comments,
			images,
			attachments
		);
	}

	public record FeedAuthorDto(
		Long id,
		String profileImageUrl,
		String username,
		TagDto tag
	) {
		public static FeedAuthorDto of(User user, TagDto tagDto) {
			return new FeedAuthorDto(
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

	public record FileDto(
		Long id,
		String name,
		String fileUrl,
		Long size
	) {
		public static FileDto from(Attachment attachment) {
			return new FileDto(
				attachment.getId(),
				attachment.getAttachmentNameValue(),
				attachment.getFileUrlValue(),
				attachment.getSize()
			);
		}
	}
}
