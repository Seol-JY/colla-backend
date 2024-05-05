package one.colla.teamspace.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

class TagNameTest {
	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	void testEqualsAndHashCode1() {
		// given
		String input = "tagName";

		//when
		TagName tageName1 = TagName.from(input);
		TagName tageName2 = TagName.from(input);

		//then
		assertThat(tageName1).isEqualTo(tageName2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	void testEqualsAndHashCode2() {
		// given
		String input1 = "tagName1";
		String input2 = "tagName2";

		//when
		TagName tageName1 = TagName.from(input1);
		TagName tageName2 = TagName.from(input2);

		// then
		assertThat(tageName1).isNotEqualTo(tageName2);
	}

	@Test
	@DisplayName("유효한 닉네임을 생성할 수 있다.")
	void testValidTagName() {
		// given
		String input = "ValidTagName";

		// when
		TagName tagName = TagName.from(input);

		// then
		assertThat(tagName.getValue()).isEqualTo(input);
	}

	@Test
	@DisplayName("태그 이름 글자 수는 2자 미만일 수 없다.")
	void testTagNameTooShort() {
		// given
		String input = "S";

		// when  & then
		assertThatThrownBy(() -> TagName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessage("태그 이름은 2자 이상, 15자 이하여야 합니다.");
	}

	@Test
	@DisplayName("테그 이름 글자 수는 15자 초과일 수 없다.")
	void testTagNameTooLong() {
		// given
		String input = "L".repeat(15 + 1);

		// when & then
		assertThatThrownBy(() -> TagName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("태그 이름은 2자 이상, 15자 이하여야 합니다.");
	}

	@Test
	@DisplayName("태그 이름은 null이 될 수 없다.")
	void testTagNameNull() {
		// given
		String input = null;

		// when & then
		assertThatThrownBy(() -> TagName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("태그 이름은 null 일 수 없습니다.");
	}

	@Test
	@DisplayName("닉네임은 공백일 수 없다.")
	void testTagNameBlank() {
		// given
		String input = "   ";

		// when & then
		assertThatThrownBy(() -> TagName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("태그 이름은 공백일 수 없습니다.");
	}
}
