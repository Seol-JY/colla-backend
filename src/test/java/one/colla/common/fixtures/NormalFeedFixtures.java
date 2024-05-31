package one.colla.common.fixtures;

import java.util.List;

import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.normal.application.dto.request.CreateNormalFeedDetails;
import one.colla.feed.normal.domain.NormalFeed;
import one.colla.teamspace.domain.UserTeamspace;

public class NormalFeedFixtures {
	public static final String SAYHI_TITLE = "안녕하세요.";
	public static final String SAYHI_DETAIL_CONTENT = "반갑습니다 우리 잘해봐요!";
	public static final String SAYHI_FILE_IMAGE_NAME = "example";
	public static final String SAYHI_FILE_IMAGE_FILEURL = "https://cdn.colla.so/example";
	public static final Long SAYHI_FILE_IMAGE_SIZE = 123L;

	public static final String NOTICE_TITLE = "팀 그라운들 룰 공지";
	public static final String NOTICE_DETAIL_CONTENT = "지각 금지, 존댓말 사용";

	public static NormalFeed SAYHI_NORMAL_FEED(UserTeamspace userTeamspace) {
		CreateNormalFeedDetails createNormalFeedDetails = new CreateNormalFeedDetails(SAYHI_DETAIL_CONTENT);

		CommonCreateFeedRequest<CreateNormalFeedDetails> createNormalFeedRequest
			= new CommonCreateFeedRequest<>(SAYHI_TITLE, List.of(), List.of(), createNormalFeedDetails);

		return NormalFeed.of(userTeamspace.getUser(), userTeamspace.getTeamspace(), createNormalFeedRequest);
	}

	public static NormalFeed SAYHI_NORMAL_FEED_WITH_ATTACHMENTS(UserTeamspace userTeamspace) {
		CreateNormalFeedDetails createNormalFeedDetails = new CreateNormalFeedDetails(SAYHI_DETAIL_CONTENT);
		CommonCreateFeedRequest.FileDto fileDto = new CommonCreateFeedRequest.FileDto(
			SAYHI_FILE_IMAGE_NAME,
			SAYHI_FILE_IMAGE_FILEURL,
			SAYHI_FILE_IMAGE_SIZE
		);

		CommonCreateFeedRequest<CreateNormalFeedDetails> createNormalFeedRequest
			= new CommonCreateFeedRequest<>(SAYHI_TITLE, List.of(fileDto), List.of(), createNormalFeedDetails);

		return NormalFeed.of(userTeamspace.getUser(), userTeamspace.getTeamspace(), createNormalFeedRequest);
	}

	public static NormalFeed NOTICE_NORMAL_FEED(UserTeamspace userTeamspace) {
		CreateNormalFeedDetails createNormalFeedDetails = new CreateNormalFeedDetails(NOTICE_DETAIL_CONTENT);

		CommonCreateFeedRequest<CreateNormalFeedDetails> createNormalFeedRequest
			= new CommonCreateFeedRequest<>(NOTICE_TITLE, List.of(), List.of(), createNormalFeedDetails);

		return NormalFeed.of(userTeamspace.getUser(), userTeamspace.getTeamspace(), createNormalFeedRequest);
	}
}
