package one.colla.teamspace.domain;

import static one.colla.common.fixtures.TagFixtures.*;
import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import one.colla.common.RepositoryTest;

class TagRepositoryTest extends RepositoryTest {
	@Autowired
	private TagRepository tagRepository;

	Teamspace OS_TEAMSPACE, DATABASE_TEAMSPACE;
	Tag OS_TEAMSPACE_FRONTEND_TAG, DATABASE_TEAMSPACE_BACKEND_TAG;

	@BeforeEach
	void setUp() {
		// given
		OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		DATABASE_TEAMSPACE = testFixtureBuilder.buildTeamspace(DATABASE_TEAMSPACE());
		OS_TEAMSPACE_FRONTEND_TAG = testFixtureBuilder.buildTag(FRONTEND_TAG(OS_TEAMSPACE));
		DATABASE_TEAMSPACE_BACKEND_TAG = testFixtureBuilder.buildTag(BACKEND_TAG(DATABASE_TEAMSPACE));
	}

	@Test
	@DisplayName("어떤 팀스페이스에 어떤 태그이름이 존재한다면 true, 존재하지 않으면 false 를 반환한다.")
	void testExistsByTeamspaceAndTagName() {
		// when
		boolean isExistExpectedTrue = tagRepository.existsByTeamspaceAndTagName
			(
				OS_TEAMSPACE,
				OS_TEAMSPACE_FRONTEND_TAG.getTagName()
			);
		boolean isExistExpectedFalse = tagRepository.existsByTeamspaceAndTagName
			(
				OS_TEAMSPACE,
				DATABASE_TEAMSPACE_BACKEND_TAG.getTagName()
			);

		// then
		assertThat(isExistExpectedTrue).isTrue();
		assertThat(isExistExpectedFalse).isFalse();
	}
}
