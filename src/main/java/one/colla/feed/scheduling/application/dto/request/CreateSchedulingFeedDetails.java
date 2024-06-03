package one.colla.feed.scheduling.application.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;
import one.colla.feed.common.util.DateTimeUtil;

public record CreateSchedulingFeedDetails(
	String content,

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@Future(message = "마감 시간은 현재시간 이후여야 합니다.")
	LocalDateTime dueAt,

	@NotNull(message = "최소 시간이 포함되어야 합니다.")
	@Min(value = 0, message = "최소 시간은 0 이상이어야 합니다.")
	@Max(value = 47, message = "최소 시간은 47 이하이어야 합니다.")
	Byte minTimeSegment,

	@NotNull(message = "최대 시간이 포함되어야 합니다.")
	@Min(value = 0, message = "최대 시간은 0 이상이어야 합니다.")
	@Max(value = 47, message = "최대 시간은 47 이하이어야 합니다.")
	Byte maxTimeSegment,

	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	List<@Future(message = "일자는 현재 시간 이후여야 합니다.") LocalDate> targetDates
) implements CreateFeedDetails {
	public CreateSchedulingFeedDetails {
		if (dueAt == null) {
			dueAt = DateTimeUtil.INFINITY;
		}
	}

	@AssertTrue(message = "minTimeSegment는 maxTimeSegment보다 작거나 같아야 합니다.")
	boolean isMinTimeSegmentValid() {
		if (minTimeSegment == null || maxTimeSegment == null) {
			return true;
		}
		return minTimeSegment <= maxTimeSegment;
	}
}
