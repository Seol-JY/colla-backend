package one.colla.feed.normal.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.normal.application.dto.request.CreateNormalFeedDetails;
import one.colla.feed.normal.domain.NormalFeed;
import one.colla.feed.normal.domain.NormalFeedRepository;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class NormalFeedService {
	private final TeamspaceService teamspaceService;
	private final NormalFeedRepository normalFeedRepository;

	@Transactional
	public void create(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final CommonCreateFeedRequest<CreateNormalFeedDetails> createNormalFeedRequest
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();
		User user = userTeamspace.getUser();

		NormalFeed normalFeed = NormalFeed.of(user, teamspace, createNormalFeedRequest);
		normalFeedRepository.save(normalFeed);

		log.info(
			"피드(일반) 작성 - 팀스페이스 Id: {}, 생성한 사용자 Id: {}, 피드 Id: {}",
			teamspaceId, user.getId(), normalFeed.getId()
		);
	}
}
