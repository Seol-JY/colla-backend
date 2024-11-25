package one.colla.feed.factory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.common.util.DateTimeUtil;
import one.colla.feed.scheduling.application.dto.response.ReadSchedulingFeedDetails;
import one.colla.feed.scheduling.domain.SchedulingFeed;
import one.colla.feed.scheduling.domain.SchedulingFeedAvailableTime;
import one.colla.feed.scheduling.domain.SchedulingFeedTargetDate;
import one.colla.user.domain.User;

@Component
public class SchedulingReadFeedDetailsFactory extends AbstractReadFeedDetailsFactory<SchedulingFeed> {
	public SchedulingReadFeedDetailsFactory() {
		super(FeedType.SCHEDULING);
	}

	@Override
	protected ReadFeedDetails createDetails(SchedulingFeed schedulingFeed) {
		LocalDateTime dueAt =
			schedulingFeed.getDueAt().equals(DateTimeUtil.INFINITY) ? null : schedulingFeed.getDueAt();
		boolean isClosed = DateTimeUtil.isDeadlinePassed(schedulingFeed.getDueAt());

		Map<LocalDate, byte[]> totalAvailability = calculateTotalAvailability(schedulingFeed);
		Map<User, UserAvailabilityInfo> userAvailabilityInfos = calculateUserAvailabilities(schedulingFeed);
		List<ReadSchedulingFeedDetails.SchedulingAvailability> responses = createResponses(userAvailabilityInfos);

		return ReadSchedulingFeedDetails.of(
			dueAt,
			isClosed,
			schedulingFeed.getMinTimeSegment(),
			schedulingFeed.getMaxTimeSegment(),
			schedulingFeed.getNumOfParticipants(),
			totalAvailability,
			responses
		);
	}

	private Map<LocalDate, byte[]> calculateTotalAvailability(SchedulingFeed schedulingFeed) {
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
		return totalAvailability;
	}

	private static class UserAvailabilityInfo {
		final Map<LocalDate, byte[]> availabilities = new HashMap<>();
		LocalDateTime lastCreatedAt;

		void updateAvailability(LocalDate date, byte[] availability, LocalDateTime createdAt) {
			availabilities.put(date, availability);
			if (lastCreatedAt == null || lastCreatedAt.isBefore(createdAt)) {
				lastCreatedAt = createdAt;
			}
		}
	}

	private Map<User, UserAvailabilityInfo> calculateUserAvailabilities(SchedulingFeed schedulingFeed) {
		Map<User, UserAvailabilityInfo> userAvailabilityInfos = new HashMap<>();

		for (SchedulingFeedTargetDate targetDate : schedulingFeed.getSchedulingFeedTargetDates()) {
			for (SchedulingFeedAvailableTime availableTime : targetDate.getSchedulingFeedAvailableTimes()) {
				User user = availableTime.getUser();
				UserAvailabilityInfo info = userAvailabilityInfos.computeIfAbsent(user,
					k -> new UserAvailabilityInfo());

				info.updateAvailability(
					targetDate.getTargetDate(),
					availableTime.getAvailableTimeSegmentArray(),
					availableTime.getCreatedAt()
				);
			}
		}

		return userAvailabilityInfos;
	}

	private List<ReadSchedulingFeedDetails.SchedulingAvailability> createResponses(
		Map<User, UserAvailabilityInfo> userAvailabilityInfos) {
		return userAvailabilityInfos.entrySet().stream()
			.map(entry -> ReadSchedulingFeedDetails.SchedulingAvailability.of(
				entry.getValue().availabilities,
				entry.getValue().lastCreatedAt,
				ReadSchedulingFeedDetails.AuthorDto.from(entry.getKey())
			))
			.collect(Collectors.toList());
	}
}
