package one.colla.feed.collect.application.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import jakarta.validation.constraints.Future;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;
import one.colla.feed.common.util.DateTimeUtil;

public record CreateCollectFeedDetails(
	String content,

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@Future(message = "마감 시간은 현재시간 이후여야 합니다.")
	LocalDateTime dueAt
) implements CreateFeedDetails {
	public CreateCollectFeedDetails {
		if (dueAt == null) {
			dueAt = DateTimeUtil.INFINITY;
		}
	}
}
