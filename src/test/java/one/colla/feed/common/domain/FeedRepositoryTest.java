package one.colla.feed.common.domain;

import static one.colla.common.fixtures.NormalFeedFixtures.*;
import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import one.colla.common.RepositoryTest;
import one.colla.feed.normal.domain.NormalFeed;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

class FeedRepositoryTest extends RepositoryTest {
	@Autowired
	private FeedRepository feedRepository;

	User USER1, USER2, RANDOMUSER1, RANDOMUSER2;
	Teamspace OS_TEAMSPACE, DATABASE_TEAMSPACE;
	UserTeamspace USER1_OS_USERTEAMSPACE, USER2_DATABASE_USERTEAMSPACE,
		RANDOMUSER1_OS_SERTEAMSPACE, RANDOMUSER2_DATABASE_USERTEAMSPACE;
	NormalFeed USER1_OS_SAYHI_NORMAL_FEED,
		RANDOMUSER1_OS_SAYHI_NORMAL_FEED_WITH_ATTACHMENTS,
		USER2_DATABASE_NOTICE_NORMAL_FEED,
		RANDOMUSER2_DATABASE_NOTICE_NORMAL_FEED;

	@BeforeEach
	void setUp() {
		// given
		USER1 = testFixtureBuilder.buildUser(USER1());
		USER2 = testFixtureBuilder.buildUser(USER2());
		RANDOMUSER1 = testFixtureBuilder.buildUser(RANDOMUSER());
		RANDOMUSER2 = testFixtureBuilder.buildUser(RANDOMUSER());

		OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		DATABASE_TEAMSPACE = testFixtureBuilder.buildTeamspace(DATABASE_TEAMSPACE());
		USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
			LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE)
		);
		RANDOMUSER1_OS_SERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
			MEMBER_USERTEAMSPACE(RANDOMUSER1, OS_TEAMSPACE)
		);
		USER2_DATABASE_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
			LEADER_USERTEAMSPACE(USER2, DATABASE_TEAMSPACE)
		);
		RANDOMUSER2_DATABASE_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
			MEMBER_USERTEAMSPACE(RANDOMUSER2, DATABASE_TEAMSPACE)
		);

		USER1_OS_SAYHI_NORMAL_FEED = testFixtureBuilder.buildNormalFeed(SAYHI_NORMAL_FEED(USER1_OS_USERTEAMSPACE));
		RANDOMUSER1_OS_SAYHI_NORMAL_FEED_WITH_ATTACHMENTS = testFixtureBuilder.buildNormalFeed(
			SAYHI_NORMAL_FEED_WITH_ATTACHMENTS(RANDOMUSER1_OS_SERTEAMSPACE));
		USER2_DATABASE_NOTICE_NORMAL_FEED = testFixtureBuilder.buildNormalFeed(
			NOTICE_NORMAL_FEED(USER2_DATABASE_USERTEAMSPACE));
		RANDOMUSER2_DATABASE_NOTICE_NORMAL_FEED = testFixtureBuilder.buildNormalFeed(
			NOTICE_NORMAL_FEED(RANDOMUSER2_DATABASE_USERTEAMSPACE));
	}

	@Test
	@DisplayName("OS 팀스페이스에서 모든 NormalFeed 목록을 불러올 수 있다.")
	void testFindFeedsByTeamspaceAndCriteria_OsTeamspace() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		List<Feed> osFeeds = feedRepository.findFeedsByTeamspaceAndCriteria(
			OS_TEAMSPACE, null, NormalFeed.class, pageable);

		// then
		assertThat(osFeeds).containsExactly(
			RANDOMUSER1_OS_SAYHI_NORMAL_FEED_WITH_ATTACHMENTS,
			USER1_OS_SAYHI_NORMAL_FEED
		);
	}

	@Test
	@DisplayName("Database 팀스페이스에서 모든 NormalFeed 목록을 불러올 수 있다.")
	void testFindFeedsByTeamspaceAndCriteria_DatabaseTeamspace() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		List<Feed> databaseFeeds = feedRepository.findFeedsByTeamspaceAndCriteria(
			DATABASE_TEAMSPACE, null, NormalFeed.class, pageable);

		// then
		assertThat(databaseFeeds).containsExactly(
			RANDOMUSER2_DATABASE_NOTICE_NORMAL_FEED,
			USER2_DATABASE_NOTICE_NORMAL_FEED
		);
	}

	@Test
	@DisplayName("OS 팀스페이스에서 특정 Feed ID 이후의 NormalFeed 목록을 불러올 수 있다.")
	void testFindFeedsByTeamspaceAndCriteria_OsTeamspaceWithAfter() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		List<Feed> osFeedsWithAfter = feedRepository.findFeedsByTeamspaceAndCriteria(
			OS_TEAMSPACE, RANDOMUSER1_OS_SAYHI_NORMAL_FEED_WITH_ATTACHMENTS.getId(), NormalFeed.class, pageable);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(osFeedsWithAfter).doesNotContain(RANDOMUSER1_OS_SAYHI_NORMAL_FEED_WITH_ATTACHMENTS);
			softly.assertThat(osFeedsWithAfter).contains(USER1_OS_SAYHI_NORMAL_FEED);
		});
	}

	@Test
	@DisplayName("팀스페이스와 피드 ID로 피드를 찾을 수 있다.")
	void testFindByIdAndTeamspace() {
		// when
		Optional<Feed> foundFeed =
			feedRepository.findByIdAndTeamspace(USER1_OS_SAYHI_NORMAL_FEED.getId(), OS_TEAMSPACE);
		var notFoundFeed = feedRepository.findByIdAndTeamspace(USER1_OS_SAYHI_NORMAL_FEED.getId(), DATABASE_TEAMSPACE);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(foundFeed).isPresent();
			softly.assertThat(foundFeed.get()).isEqualTo(USER1_OS_SAYHI_NORMAL_FEED);
			softly.assertThat(notFoundFeed).isNotPresent();
		});
	}

	@Test
	@DisplayName("팀스페이스와 피드 ID로 피드 존재 여부를 확인합니다.")
	void testExistsByIdAndTeamspace() {
		// when
		boolean exists = feedRepository.existsByIdAndTeamspace(USER1_OS_SAYHI_NORMAL_FEED.getId(), OS_TEAMSPACE);
		boolean notExists = feedRepository.existsByIdAndTeamspace(USER1_OS_SAYHI_NORMAL_FEED.getId(),
			DATABASE_TEAMSPACE);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(exists).isTrue();
			softly.assertThat(notExists).isFalse();
		});
	}
}
