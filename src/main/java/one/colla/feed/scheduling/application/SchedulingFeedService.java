package one.colla.feed.scheduling.application;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.application.FeedService;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.common.util.DateTimeUtil;
import one.colla.feed.scheduling.application.dto.request.CreateSchedulingFeedDetails;
import one.colla.feed.scheduling.application.dto.request.PutSchedulingAvailabilitiesRequest;
import one.colla.feed.scheduling.domain.SchedulingFeed;
import one.colla.feed.scheduling.domain.SchedulingFeedAvailableTime;
import one.colla.feed.scheduling.domain.SchedulingFeedRepository;
import one.colla.feed.scheduling.domain.SchedulingFeedTargetDate;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingFeedService {
	private final TeamspaceService teamspaceService;
	private final FeedService feedService;
	private final SchedulingFeedRepository schedulingFeedRepository;

	@Transactional
	public void create(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final CommonCreateFeedRequest<CreateSchedulingFeedDetails> createSchedulingFeedRequest
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();
		User user = userTeamspace.getUser();

		SchedulingFeed schedulingFeed = SchedulingFeed.of(user, teamspace, createSchedulingFeedRequest);

		schedulingFeed.addTargetDates(
			createSchedulingFeedRequest.details().targetDates()
				.stream()
				.map(td -> SchedulingFeedTargetDate.of(schedulingFeed, td))
				.toList()
		);

		schedulingFeedRepository.save(schedulingFeed);

		log.info(
			"피드(일정 조율) 작성 - 팀스페이스 Id: {}, 생성한 사용자 Id: {}, 피드 Id: {}",
			teamspaceId, user.getId(), schedulingFeed.getId()
		);
	}

	@Transactional
	public void updateSchedulingAvailability(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final Long feedId,
		final PutSchedulingAvailabilitiesRequest request
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();
		User user = userTeamspace.getUser();

		SchedulingFeed feed
			= (SchedulingFeed)feedService.findFeedByTeamspaceAndType(teamspace, feedId, FeedType.SCHEDULING);

		if (DateTimeUtil.isDeadlinePassed(feed.getDueAt())) {
			log.warn(
				"피드(일정 조율) 등록 시도 - 팀스페이스 Id: {}, 사용자 Id: {}, 피드 Id: {} (마감일이 지남)",
				teamspaceId, userDetails.getUserId(), feedId
			);
			throw new CommonException(ExceptionCode.FORBIDDEN_ACTION_DEADLINE_PASSED);
		}

		Map<LocalDate, byte[]> availabilities = request.availabilities();

		boolean alreadyParticipated = false;
		for (SchedulingFeedTargetDate targetDate : feed.getSchedulingFeedTargetDates()) {
			byte[] availabilityArray = availabilities.get(targetDate.getTargetDate());

			if (availabilityArray == null) {
				throw new CommonException(ExceptionCode.FORBIDDEN_SCHEDULING_RESPONSE);
			}

			Optional<SchedulingFeedAvailableTime> schedulingFeedAvailableTimeByUser
				= targetDate.getSchedulingFeedAvailableTimeByUser(user);

			schedulingFeedAvailableTimeByUser
				.ifPresentOrElse(availableTime -> availableTime.changeAvailableTimeSegmentArray(availabilityArray),
					() -> {
						SchedulingFeedAvailableTime schedulingFeedAvailableTime
							= SchedulingFeedAvailableTime.of(targetDate, user, availabilityArray);
						targetDate.addSchedulingFeedAvailableTime(schedulingFeedAvailableTime);
					});

			if (schedulingFeedAvailableTimeByUser.isPresent()) {
				alreadyParticipated = true;
			}
		}

		if (!alreadyParticipated) {
			feed.increaseNumOfParticipants();
		}

		log.info(
			"피드(일정 조율 추가)  - 팀스페이스 Id: {}, 생성한 사용자 Id: {}, 피드 Id: {}",
			teamspaceId, user.getId(), feed.getId()
		);

		schedulingFeedRepository.save(feed);
	}

	@Transactional
	public void deleteSchedulingAvailability(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final Long feedId
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();
		User user = userTeamspace.getUser();

		SchedulingFeed feed
			= (SchedulingFeed)feedService.findFeedByTeamspaceAndType(teamspace, feedId, FeedType.SCHEDULING);

		if (DateTimeUtil.isDeadlinePassed(feed.getDueAt())) {
			log.warn(
				"피드(일정 조율) 삭제 시도 - 팀스페이스 Id: {}, 사용자 Id: {}, 피드 Id: {} (마감일이 지남)",
				teamspaceId, userDetails.getUserId(), feedId
			);
			throw new CommonException(ExceptionCode.FORBIDDEN_ACTION_DEADLINE_PASSED);
		}

		boolean removedSuccessfully = false;
		for (SchedulingFeedTargetDate targetDate : feed.getSchedulingFeedTargetDates()) {
			removedSuccessfully = targetDate.removeSchedulingFeedAvailableTimeByUser(user);
		}

		if (removedSuccessfully) {
			feed.decreaseNumOfParticipants();
		}

		log.info(
			"피드(일정 조율 제거)  - 팀스페이스 Id: {}, 생성한 사용자 Id: {}, 피드 Id: {}",
			teamspaceId, user.getId(), feed.getId()
		);

		schedulingFeedRepository.save(feed);

	}
}
