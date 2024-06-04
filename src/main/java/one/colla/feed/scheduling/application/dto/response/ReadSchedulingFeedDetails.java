package one.colla.feed.scheduling.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.scheduling.converter.ByteArrayToJsonSerializer;
import one.colla.user.domain.User;

public record ReadSchedulingFeedDetails(
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime dueAt,
	boolean isClosed,
	Byte minTimeSegment,
	Byte maxTimeSegment,
	Byte numOfParticipants,
	@JsonSerialize(contentUsing = ByteArrayToJsonSerializer.class)
	Map<LocalDate, byte[]> totalAvailability,
	List<SchedulingAvailability> responses
) implements ReadFeedDetails {

	public static ReadSchedulingFeedDetails of(
		LocalDateTime dueAt,
		boolean isClosed,
		Byte minTimeSegment,
		Byte maxTimeSegment,
		Byte numOfParticipants,
		@JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
		Map<LocalDate, byte[]> totalAvailability,
		List<SchedulingAvailability> responses
	) {
		return new ReadSchedulingFeedDetails(
			dueAt,
			isClosed,
			minTimeSegment,
			maxTimeSegment,
			numOfParticipants,
			totalAvailability,
			responses
		);
	}

	public record SchedulingAvailability(
		@JsonSerialize(contentUsing = ByteArrayToJsonSerializer.class)
		Map<LocalDate, byte[]> availabilities,
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime createdAt,
		AuthorDto user
	) {
		public static SchedulingAvailability of(
			Map<LocalDate, byte[]> availabilities,
			LocalDateTime createdAt,
			AuthorDto user
		) {
			return new SchedulingAvailability(availabilities, createdAt, user);
		}
	}

	public record AuthorDto(
		Long id,
		String profileImageUrl,
		String username
	) {
		public static AuthorDto from(User user) {
			return new AuthorDto(
				user.getId(),
				user.getProfileImageUrlValue(),
				user.getUsernameValue()
			);
		}
	}

}
