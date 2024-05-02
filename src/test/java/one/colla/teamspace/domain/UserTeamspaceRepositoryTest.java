package one.colla.teamspace.domain;

import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import one.colla.common.RepositoryTest;
import one.colla.user.domain.User;

class UserTeamspaceRepositoryTest extends RepositoryTest {
	@Autowired
	private UserTeamspaceRepository userTeamspaceRepository;

	User USER1, USER2;
	Teamspace OS_TEAMSPACE, DATABASE_TEAMSPACE;
	UserTeamspace USER1_OS_USERTEAMSPACE, USER2_OS_USERTEAMSPACE;

	@BeforeEach
	void setUp() {
		// given
		USER1 = testFixtureBuilder.buildUser(USER1());
		USER2 = testFixtureBuilder.buildUser(USER2());
		OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		DATABASE_TEAMSPACE = testFixtureBuilder.buildTeamspace(DATABASE_TEAMSPACE());
		USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
		USER2_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));
	}

	@Test
	@DisplayName("어떤 사용자가 어떤 팀스페이스에 참가되어 있다면 true, 존재하지 않으면 false 를 반환한다.")
	void testExistsByUserAndTeamspace() {
		// when
		boolean isParticipantExpectedTrue = userTeamspaceRepository.existsByUserAndTeamspace(USER1, OS_TEAMSPACE);
		boolean isParticipantExpectedFalse = userTeamspaceRepository.existsByUserAndTeamspace(USER2,
			DATABASE_TEAMSPACE);

		// then
		assertThat(isParticipantExpectedTrue).isTrue();
		assertThat(isParticipantExpectedFalse).isFalse();
	}

	@Test
	@DisplayName("userId와 teamspaceId로 UserTeamspace 를 조회할 수 있다.")
	void testFindByUserIdAndTeamspaceId() {
		// when
		Optional<UserTeamspace> userTeamspaceExpectedNotNull =
			userTeamspaceRepository.findByUserIdAndTeamspaceId(
				USER1.getId(),
				OS_TEAMSPACE.getId()
			);
		Optional<UserTeamspace> userTeamspaceExpectedNull =
			userTeamspaceRepository.findByUserIdAndTeamspaceId(
				USER2.getId(),
				DATABASE_TEAMSPACE.getId()
			);

		// then
		assertThat(userTeamspaceExpectedNotNull).hasValueSatisfying(userTeamspace -> {
			assertThat(userTeamspace).isEqualTo(USER1_OS_USERTEAMSPACE);
		});
		assertThat(userTeamspaceExpectedNull).isEmpty();
	}

	@Test
	@DisplayName("특정 Teamspace 에 속한 UserTeamspace 전체를 조회할 수 있다.")
	void testFindAllByTeamspace() {
		// when
		List<UserTeamspace> userTeamspaces = userTeamspaceRepository.findAllByTeamspace(OS_TEAMSPACE);

		// then
		assertThat(userTeamspaces).hasSize(2)
			.containsExactly(
				USER1_OS_USERTEAMSPACE,
				USER2_OS_USERTEAMSPACE
			);
	}
}
