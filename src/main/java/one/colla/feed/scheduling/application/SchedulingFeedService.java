package one.colla.feed.scheduling.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.scheduling.application.dto.request.CreateSchedulingFeedDetails;
import one.colla.feed.scheduling.domain.SchedulingFeed;
import one.colla.feed.scheduling.domain.SchedulingFeedRepository;
import one.colla.feed.scheduling.domain.SchedulingFeedTargetDate;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingFeedService {
	private final TeamspaceService teamspaceService;
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

}
