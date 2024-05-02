package one.colla.teamspace.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

class TeamspaceNameTest {
	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	void testEqualsAndHashCode1() {
		// given
		String input = "TeamspaceName";

		//when
		TeamspaceName tageName1 = TeamspaceName.from(input);
		TeamspaceName tageName2 = TeamspaceName.from(input);

		//then
		assertThat(tageName1).isEqualTo(tageName2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	void testEqualsAndHashCode2() {
		// given
		String input1 = "TeamspaceName1";
		String input2 = "TeamspaceName2";

		//when
		TeamspaceName tageName1 = TeamspaceName.from(input1);
		TeamspaceName tageName2 = TeamspaceName.from(input2);

		// then
		assertThat(tageName1).isNotEqualTo(tageName2);
	}

	@Test
	@DisplayName("유효한 팀스페이스 이름을 생성할 수 있다.")
	void testValidTeamspaceName() {
		// given
		String input = "ValidTeamspaceName";

		// when
		TeamspaceName teamspaceName = TeamspaceName.from(input);

		// then
		assertThat(teamspaceName.getValue()).isEqualTo(input);
	}

	@Test
	@DisplayName("팀스페이스 이름 글자 수는 2자 미만일 수 없다.")
	void testTeamspaceNameTooShort() {
		// given
		String input = "S";

		// when  & then
		assertThatThrownBy(() -> TeamspaceName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessage("팀스페이스 이름은 2자 이상, 20자 이하여야 합니다.");
	}

	@Test
	@DisplayName("팀스페이스 이름 글자 수는 20자 초과일 수 없다.")
	void testTeamspaceNameTooLong() {
		// given
		String input = "L".repeat(20 + 1);

		// when & then
		assertThatThrownBy(() -> TeamspaceName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("팀스페이스 이름은 2자 이상, 20자 이하여야 합니다.");
	}

	@Test
	@DisplayName("팀스페이스 이름은 null이 될 수 없다.")
	void testTeamspaceNameNull() {
		// given
		String input = null;

		// when & then
		assertThatThrownBy(() -> TeamspaceName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("팀스페이스 이름은 null 일 수 없습니다.");
	}

	@Test
	@DisplayName("팀스페이스 이름은 공백일 수 없다.")
	void testTeamspaceNameBlank() {
		// given
		String input = "   ";

		// when & then
		assertThatThrownBy(() -> TeamspaceName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("팀스페이스 이름은 공백일 수 없습니다.");
	}
}
