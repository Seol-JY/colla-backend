package one.colla.feed.collect.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.collect.application.dto.request.CreateCollectFeedDetails;
import one.colla.feed.collect.application.dto.request.UpdateCollectFeedResponseRequest;
import one.colla.feed.collect.application.dto.response.ReadCollectFeedResponseResponse;
import one.colla.feed.collect.domain.CollectFeed;
import one.colla.feed.collect.domain.CollectFeedRepository;
import one.colla.feed.collect.domain.CollectFeedResponse;
import one.colla.feed.collect.domain.CollectFeedStatus;
import one.colla.feed.common.application.FeedService;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.common.util.DateTimeUtil;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.teamspace.domain.UserTeamspaceRepository;
import one.colla.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectFeedService {
	private final TeamspaceService teamspaceService;
	private final FeedService feedService;
	private final CollectFeedRepository collectFeedRepository;
	private final UserTeamspaceRepository userTeamspaceRepository;

	@Transactional
	public void create(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final CommonCreateFeedRequest<CreateCollectFeedDetails> createCollectFeedRequest
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();
		List<User> users = teamspace.getUserTeamspaces().stream().map(UserTeamspace::getUser).toList();
		User user = userTeamspace.getUser();

		CollectFeed collectFeed = CollectFeed.of(user, teamspace, createCollectFeedRequest);

		collectFeed.addResponses(
			users.stream().map(u -> CollectFeedResponse.createEmptyResponse(u, collectFeed)).toList()
		);

		collectFeedRepository.save(collectFeed);

		log.info(
			"피드(자료수집) 작성 - 팀스페이스 Id: {}, 생성한 사용자 Id: {}, 피드 Id: {}",
			teamspaceId, user.getId(), collectFeed.getId()
		);
	}

	@Transactional
	public ReadCollectFeedResponseResponse readResponse(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final Long feedId,
		final Long userId
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();

		CollectFeed feed = (CollectFeed)feedService.findFeedByTeamspaceAndType(teamspace, feedId, FeedType.COLLECT);

		Optional<CollectFeedResponse> optionalResponse = feed.getCollectFeedResponses().stream()
			.filter(cfr -> cfr.getUser().getId().equals(userId))
			.findFirst();

		if (optionalResponse.isEmpty()) {
			log.warn(
				"존재하지 않는 응답 조회 시도 - 팀스페이스 Id: {}, 사용자 Id: {}, 피드 Id: {}",
				teamspaceId, userDetails.getUserId(), feedId
			);
			throw new CommonException(ExceptionCode.FORBIDDEN_COLLECT_RESPONSE);
		}

		CollectFeedResponse response = optionalResponse.get();

		UserTeamspace authorUserTeamspace = userTeamspaceRepository.findByUserIdAndTeamspaceId(userId, teamspaceId)
			.orElseThrow(() -> new IllegalArgumentException("탈퇴한 회원입니다."));
		// TODO: 추후 탈퇴 회원에 대한 처리 필요

		ReadCollectFeedResponseResponse.TagDto tagDto
			= ReadCollectFeedResponseResponse.TagDto.from(authorUserTeamspace.getTag());
		ReadCollectFeedResponseResponse.CollectResponseAuthorDto authorDto
			= ReadCollectFeedResponseResponse.CollectResponseAuthorDto.of(response.getUser(), tagDto);

		log.info(
			"피드 응답 조회 - 팀스페이스 Id: {}, 사용자 Id: {}, 피드 Id: {}, 조회된 응답 작성자 Id: {}",
			teamspaceId, userDetails.getUserId(), feedId, userId
		);

		return ReadCollectFeedResponseResponse.of(authorDto, response);
	}

	@Transactional
	public void updateResponse(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final Long feedId,
		final UpdateCollectFeedResponseRequest request
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();

		CollectFeed feed = (CollectFeed)feedService.findFeedByTeamspaceAndType(teamspace, feedId, FeedType.COLLECT);
		if (DateTimeUtil.isDeadlinePassed(feed.getDueAt())) {
			log.warn(
				"피드(자료수집 응답) 수정 시도 - 팀스페이스 Id: {}, 사용자 Id: {}, 피드 Id: {} (마감일이 지남)",
				teamspaceId, userDetails.getUserId(), feedId
			);
			throw new CommonException(ExceptionCode.FORBIDDEN_ACTION_DEADLINE_PASSED);
		}

		Optional<CollectFeedResponse> optionalResponse = feed.getCollectFeedResponses().stream()
			.filter(cfr -> cfr.getUser().getId().equals(userDetails.getUserId()))
			.findFirst();

		if (optionalResponse.isEmpty()) {
			log.warn(
				"존재하지 않는 응답 수정 시도 - 팀스페이스 Id: {}, 사용자 Id: {}, 피드 Id: {}",
				teamspaceId, userDetails.getUserId(), feedId
			);
			throw new CommonException(ExceptionCode.FORBIDDEN_COLLECT_RESPONSE);
		}

		CollectFeedResponse response = optionalResponse.get();
		response.changeTitleAndContent(request);

		if (request.title() == null && request.content() == null) {
			response.changeStatus(CollectFeedStatus.PENDING);
		} else {
			response.changeStatus(CollectFeedStatus.COMPLETED);
		}

		collectFeedRepository.save(feed);

		log.info(
			"피드 응답 수정 완료 - 팀스페이스 Id: {}, 사용자 Id: {}, 피드 Id: {}",
			teamspaceId, userDetails.getUserId(), feedId
		);
	}
}
