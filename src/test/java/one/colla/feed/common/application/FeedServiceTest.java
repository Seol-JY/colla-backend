package one.colla.feed.common.application;

import static one.colla.common.fixtures.NormalFeedFixtures.*;
import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import one.colla.common.ServiceTest;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedRepository;
import one.colla.feed.common.factory.ReadFeedDetailsFactoryProvider;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

class FeedServiceTest extends ServiceTest {
	@MockBean
	private TeamspaceService teamspaceService;

	@Autowired
	private ReadFeedDetailsFactoryProvider readFeedDetailsFactoryProvider;

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private FeedService feedService;

	User USER1, USER2, RANDOMUSER1, RANDOMUSER2;
	CustomUserDetails USER1_DETAILS;
	Teamspace OS_TEAMSPACE, DATABASE_TEAMSPACE;
	UserTeamspace USER1_OS_USERTEAMSPACE, USER2_DATABASE_USERTEAMSPACE, RANDOMUSER1_OS_USERTEAMSPACE, RANDOMUSER2_DATABASE_USERTEAMSPACE;
	Feed USER1_OS_SAYHI_NORMAL_FEED, RANDOMUSER1_OS_SAYHI_NORMAL_FEED_WITH_ATTACHMENTS, USER2_DATABASE_NOTICE_NORMAL_FEED, RANDOMUSER2_DATABASE_NOTICE_NORMAL_FEED;

	@BeforeEach
	void setUp() {
		// given
		USER1 = testFixtureBuilder.buildUser(USER1());
		USER2 = testFixtureBuilder.buildUser(USER2());

		USER1_DETAILS = createCustomUserDetailsByUser(USER1);

		RANDOMUSER1 = testFixtureBuilder.buildUser(RANDOMUSER());
		RANDOMUSER2 = testFixtureBuilder.buildUser(RANDOMUSER());

		OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		DATABASE_TEAMSPACE = testFixtureBuilder.buildTeamspace(DATABASE_TEAMSPACE());

		USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
		RANDOMUSER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
			MEMBER_USERTEAMSPACE(RANDOMUSER1, OS_TEAMSPACE));
		USER2_DATABASE_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
			LEADER_USERTEAMSPACE(USER2, DATABASE_TEAMSPACE));
		RANDOMUSER2_DATABASE_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
			MEMBER_USERTEAMSPACE(RANDOMUSER2, DATABASE_TEAMSPACE));

		USER1_OS_SAYHI_NORMAL_FEED = testFixtureBuilder.buildNormalFeed(SAYHI_NORMAL_FEED(USER1_OS_USERTEAMSPACE));
		RANDOMUSER1_OS_SAYHI_NORMAL_FEED_WITH_ATTACHMENTS = testFixtureBuilder.buildNormalFeed(
			SAYHI_NORMAL_FEED_WITH_ATTACHMENTS(RANDOMUSER1_OS_USERTEAMSPACE));
		USER2_DATABASE_NOTICE_NORMAL_FEED = testFixtureBuilder.buildNormalFeed(
			NOTICE_NORMAL_FEED(USER2_DATABASE_USERTEAMSPACE));
		RANDOMUSER2_DATABASE_NOTICE_NORMAL_FEED = testFixtureBuilder.buildNormalFeed(
			NOTICE_NORMAL_FEED(RANDOMUSER2_DATABASE_USERTEAMSPACE));
	}

	@Nested
	@DisplayName("피드 목록 조회 시")
		// TODO: 테스트 작성 필요
	class ReadFeedsTest {
		@Test
		@DisplayName("피드 목록을 정상적으로 조회한다.")
		void readFeedsSuccessfully() {
			// given
			given(teamspaceService.getUserTeamspace(any(), any())).willReturn(USER1_OS_USERTEAMSPACE);

			// when
			feedService.readFeeds(
				USER1_DETAILS,
				OS_TEAMSPACE.getId(),
				null,
				null,
				5
			);

		}

		@Test
		@DisplayName("afterFeedId가 존재하지 않는 경우 예외를 발생시킨다.")
		void readFeedsAfterFeedNotFound() {
		}

		@Test
		@DisplayName("limit이 정상적으로 동작한다.")
		void readFeedsWithLimit() {

		}
	}

	@Nested
	@DisplayName("피드 단건 조회 시")
	class ReadFeedTest {

		@Test
		@DisplayName("피드를 정상적으로 조회한다.")
		void readFeedSuccessfully() {
		}

		@Test
		@DisplayName("피드가 존재하지 않는 경우 예외를 발생시킨다.")
		void readFeedNotFound() {
		}

		@Test
		@DisplayName("해당 팀스페이스의 피드가 아닌 피드 id로 조회 시도 시 예외가 발생한다.")
		void readFeedNotBelongingToTeamspace() {
		}
	}
}
