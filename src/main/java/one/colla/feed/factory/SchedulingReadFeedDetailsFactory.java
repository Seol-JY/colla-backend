package one.colla.feed.factory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.common.util.DateTimeUtil;
import one.colla.feed.scheduling.application.dto.response.ReadSchedulingFeedDetails;
import one.colla.feed.scheduling.domain.SchedulingFeed;
import one.colla.feed.scheduling.domain.SchedulingFeedAvailableTime;
import one.colla.feed.scheduling.domain.SchedulingFeedTargetDate;
import one.colla.user.domain.User;

public class SchedulingReadFeedDetailsFactory implements ReadFeedDetailsFactory {
	@Override
	public Pair<FeedType, ReadFeedDetails> createReadFeedDetails(Feed feed) {
		if (feed instanceof SchedulingFeed schedulingFeed) {
			LocalDateTime dueAt =
				schedulingFeed.getDueAt().equals(DateTimeUtil.INFINITY) ? null : schedulingFeed.getDueAt();
			boolean isClosed = DateTimeUtil.isDeadlinePassed(schedulingFeed.getDueAt());

			Map<LocalDate, byte[]> totalAvailability = new HashMap<>();
			for (SchedulingFeedTargetDate targetDate : schedulingFeed.getSchedulingFeedTargetDates()) {
				byte[] totalAvailabilityForDate = new byte[48];
				for (SchedulingFeedAvailableTime availableTime : targetDate.getSchedulingFeedAvailableTimes()) {
					byte[] availableTimeArray = availableTime.getAvailableTimeSegmentArray();
					for (int i = 0; i < totalAvailabilityForDate.length; i++) {
						totalAvailabilityForDate[i] += availableTimeArray[i];
					}
				}
				totalAvailability.put(targetDate.getTargetDate(), totalAvailabilityForDate);
			}

			Map<User, Map<LocalDate, byte[]>> userAvailabilitiesMap = new HashMap<>();
			Map<User, LocalDateTime> userCreatedAtMap = new HashMap<>();

			for (SchedulingFeedTargetDate targetDate : schedulingFeed.getSchedulingFeedTargetDates()) {
				for (SchedulingFeedAvailableTime availableTime : targetDate.getSchedulingFeedAvailableTimes()) {
					User user = availableTime.getUser();
					LocalDateTime createdAt = availableTime.getCreatedAt();
					Map<LocalDate, byte[]> availabilities = userAvailabilitiesMap.getOrDefault(user, new HashMap<>());

					availabilities.put(targetDate.getTargetDate(), availableTime.getAvailableTimeSegmentArray());
					userAvailabilitiesMap.put(user, availabilities);

					// 가장 최근 작성 일시를 저장
					if (!userCreatedAtMap.containsKey(user) || userCreatedAtMap.get(user).isBefore(createdAt)) {
						userCreatedAtMap.put(user, createdAt);
					}
				}
			}

			List<ReadSchedulingFeedDetails.SchedulingAvailability> responses = userAvailabilitiesMap.entrySet().stream()
				.map(entry -> ReadSchedulingFeedDetails.SchedulingAvailability.of(
					entry.getValue(),
					userCreatedAtMap.get(entry.getKey()),
					ReadSchedulingFeedDetails.AuthorDto.from(entry.getKey())
				))
				.collect(Collectors.toList());

			ReadSchedulingFeedDetails details = ReadSchedulingFeedDetails.of(
				dueAt,
				isClosed,
				schedulingFeed.getMinTimeSegment(),
				schedulingFeed.getMaxTimeSegment(),
				schedulingFeed.getNumOfParticipants(),
				totalAvailability,
				responses
			);

			return Pair.of(FeedType.SCHEDULING, details);

		} else {
			throw new IllegalArgumentException("Unsupported feed type");
		}
	}
}
