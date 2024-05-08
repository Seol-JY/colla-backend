package one.colla.teamspace.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

class TeamspaceProfileImageUrlTest {
	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	void testEqualsAndHashCode1() {
		// given
		String input = "https://example.com/profile.jpg";

		// when
		TeamspaceProfileImageUrl url1 = new TeamspaceProfileImageUrl(input);
		TeamspaceProfileImageUrl url2 = new TeamspaceProfileImageUrl(input);

		// then
		assertThat(url1).isEqualTo(url2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	void testEqualsAndHashCode2() {
		// given
		String input1 = "https://example.com/profile.jpg";
		String input2 = "https://example.com/another.jpg";

		// when
		TeamspaceProfileImageUrl url1 = new TeamspaceProfileImageUrl(input1);
		TeamspaceProfileImageUrl url2 = new TeamspaceProfileImageUrl(input2);

		// then
		assertThat(url1).isNotEqualTo(url2);
	}

	@Test
	@DisplayName("유효한 프로필 이미지 URL 을 생성할 수 있다.")
	void testValidProfileImageUrl() {
		// given
		String validUrl = "https://example.com/profile.jpg";

		// when
		TeamspaceProfileImageUrl imageUrl = new TeamspaceProfileImageUrl(validUrl);

		// then
		assertThat(imageUrl.getValue()).isEqualTo(validUrl);
	}

	@Test
	@DisplayName("URL 형식이 유효하지 않으면 예외가 발생한다.")
	void testInvalidProfileImageUrl() {
		// given
		String invalidUrl = "htp:/example.com";

		// when/then
		assertThatThrownBy(() -> new TeamspaceProfileImageUrl(invalidUrl))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("url 형식이 아닙니다.");
	}

	@Test
	@DisplayName("URL 은 공백일 수 없다.")
	void testBlankProfileImageUrl() {
		// given
		String blankUrl = "   ";

		// when/then
		assertThatThrownBy(() -> new TeamspaceProfileImageUrl(blankUrl))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("url은 공백일 수 없습니다.");
	}

	@Test
	@DisplayName("프로필 이미지를 변경 할 수 있다.")
	void testProfileImageUrlChange() {
		// given
		String initialUrl = "https://example.com/old_profile.jpg";
		String newUrl = "https://example.com/new_profile.jpg";
		TeamspaceProfileImageUrl imageUrl = new TeamspaceProfileImageUrl(initialUrl);

		// when
		TeamspaceProfileImageUrl updatedImageUrl = imageUrl.change(newUrl);

		// then
		assertThat(updatedImageUrl.getValue()).isNotEqualTo(initialUrl);
		assertThat(updatedImageUrl.getValue()).isEqualTo(newUrl);
	}
}
