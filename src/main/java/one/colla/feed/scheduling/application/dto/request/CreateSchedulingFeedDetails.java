package one.colla.feed.scheduling.application.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;
import one.colla.feed.common.util.DateTimeUtil;
import one.colla.feed.common.util.LocalDateListDeserializer;

public record CreateSchedulingFeedDetails(
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

	@NotNull(message = "일자 목록이 포함되어야 합니다.")
	@Size(min = 1, message = "일자 목록은 최소 1개 이상의 날짜를 포함해야 합니다.")
	@JsonDeserialize(using = LocalDateListDeserializer.class)
	List<@FutureOrPresent(message = "일자는 과거일 수 없습니다.") LocalDate> targetDates

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

	@AssertTrue(message = "targetDates에 중복된 날짜가 없어야 합니다.")
	boolean isTargetDatesUnique() {
		System.out.println("발생");
		if (targetDates == null) {
			return true;
		}
		Set<LocalDate> uniqueDates = new HashSet<>(targetDates);
		return uniqueDates.size() == targetDates.size();
	}
}
