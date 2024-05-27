package one.colla.feed.common.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.application.dto.response.CommentDto;
import one.colla.feed.common.application.dto.response.CommonReadFeedListResponse;
import one.colla.feed.common.application.dto.response.CommonReadFeedResponse;
import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedRepository;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.common.factory.ReadFeedDetailsFactory;
import one.colla.feed.common.factory.ReadFeedDetailsFactoryProvider;
import one.colla.file.domain.AttachmentType;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.teamspace.domain.UserTeamspaceRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
	private final TeamspaceService teamspaceService;
	private final UserTeamspaceRepository userTeamspaceRepository;
	private final FeedRepository feedRepository;
	private final ReadFeedDetailsFactoryProvider readFeedDetailsFactoryProvider;

	@Transactional(readOnly = true)
	public CommonReadFeedListResponse readFeeds(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		@Nullable final Long afterFeedId,
		@Nullable final FeedType feedType,
		final int limit
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();
		PageRequest pageRequest = PageRequest.of(0, limit);

		if (afterFeedId != null && !feedRepository.existByIdAndTeamspace(afterFeedId, teamspace)) {
			throw new CommonException(ExceptionCode.NOT_FOUND_FEED);
		}

		List<Feed> feeds = feedRepository.findFeedsByTeamspaceAndCriteria(
			userTeamspace.getTeamspace(),
			afterFeedId,
			feedType != null ? feedType.getFeedClass() : null,
			pageRequest
		);

		List<CommonReadFeedResponse<ReadFeedDetails>> feedResponses = feeds.stream()
			.map(this::toCommonReadFeedResponse)
			.toList();

		log.info(
			"피드 목록 조회 - 팀스페이스 Id: {}, 사용자 Id: {}, afterFeedId: {}, feedType: {}, limit: {}",
			teamspaceId, userDetails.getUserId(), afterFeedId, feedType, limit
		);

		return CommonReadFeedListResponse.from(feedResponses);
	}

	@Transactional(readOnly = true)
	public CommonReadFeedResponse<ReadFeedDetails> readFeed(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final Long feedId
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();

		Feed feed = feedRepository.findByIdAndTeamspace(feedId, teamspace)
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_FEED));

		log.info(
			"피드 단건 조회 - 팀스페이스 Id: {}, 사용자 Id: {}, 조회 피드 Id: {}",
			teamspaceId, userDetails.getUserId(), feedId
		);
		return toCommonReadFeedResponse(feed);
	}

	private CommonReadFeedResponse<ReadFeedDetails> toCommonReadFeedResponse(Feed feed) {
		UserTeamspace userTeamspace = userTeamspaceRepository.findByUserIdAndTeamspaceId(
			feed.getUser().getId(), feed.getTeamspace().getId()
		).orElseThrow(() -> new IllegalArgumentException("탈퇴한 회원입니다."));
		// TODO: 추후 탈퇴 회원에 대한 처리 필요

		CommonReadFeedResponse.TagDto tagDto
			= CommonReadFeedResponse.TagDto.from(userTeamspace.getTag());

		CommonReadFeedResponse.FeedAuthorDto feedAuthorDto
			= CommonReadFeedResponse.FeedAuthorDto.of(feed.getUser(), tagDto);

		Map<AttachmentType, List<CommonReadFeedResponse.FileDto>> groupedAttachments = groupAttachmentsByType(feed);

		Pair<FeedType, ReadFeedDetails> details = createReadFeedDetails(feed);

		List<CommentDto> commentDtos = feed.getComments().stream().map(CommentDto::from).toList();

		return CommonReadFeedResponse.of(
			details.getLeft(),
			feed,
			feedAuthorDto,
			details.getRight(),
			commentDtos,
			groupedAttachments.getOrDefault(AttachmentType.IMAGE, List.of()),
			groupedAttachments.getOrDefault(AttachmentType.FILE, List.of())
		);
	}

	private Map<AttachmentType, List<CommonReadFeedResponse.FileDto>> groupAttachmentsByType(Feed feed) {
		return feed.getFeedAttachments().stream()
			.collect(
				Collectors.groupingBy(
					feedAttachment -> feedAttachment.getAttachment().getAttachmentType(),
					Collectors.mapping(
						feedAttachment -> CommonReadFeedResponse.FileDto.from(feedAttachment.getAttachment()),
						Collectors.toList()
					)
				)
			);
	}

	private Pair<FeedType, ReadFeedDetails> createReadFeedDetails(Feed feed) {
		ReadFeedDetailsFactory factory = readFeedDetailsFactoryProvider.getFactory(feed);
		return factory.createReadFeedDetails(feed);
	}
}
