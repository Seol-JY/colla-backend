package one.colla.user.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

public class UserProfileImageUrlTest {

	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	public void testEqualsAndHashCode1() {
		// given
		String input = "http://example.com/profile.jpg";

		// when
		UserProfileImageUrl url1 = new UserProfileImageUrl(input);
		UserProfileImageUrl url2 = new UserProfileImageUrl(input);

		// then
		assertThat(url1).isEqualTo(url2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	public void testEqualsAndHashCode2() {
		// given
		String input1 = "http://example.com/profile.jpg";
		String input2 = "http://example.com/another.jpg";

		// when
		UserProfileImageUrl url1 = new UserProfileImageUrl(input1);
		UserProfileImageUrl url2 = new UserProfileImageUrl(input2);

		// then
		assertThat(url1).isNotEqualTo(url2);
	}

	@Test
	@DisplayName("유효한 프로필 이미지 URL을 생성할 수 있다.")
	public void testValidProfileImageUrl() {
		// given
		String validUrl = "http://example.com/profile.jpg";

		// when
		UserProfileImageUrl imageUrl = new UserProfileImageUrl(validUrl);

		// then
		assertThat(imageUrl.getValue()).isEqualTo(validUrl);
	}

	@Test
	@DisplayName("URL 형식이 유효하지 않으면 예외가 발생한다.")
	public void testInvalidProfileImageUrl() {
		// given
		String invalidUrl = "htp:/example.com";

		// when/then
		assertThatThrownBy(() -> new UserProfileImageUrl(invalidUrl))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("url 형식이 아닙니다.");
	}

	@Test
	@DisplayName("URL은 공백일 수 없다.")
	public void testBlankProfileImageUrl() {
		// given
		String blankUrl = "   ";

		// when/then
		assertThatThrownBy(() -> new UserProfileImageUrl(blankUrl))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("url은 공백일 수 없습니다.");
	}

	@Test
	@DisplayName("프로필 이미지를 변경 할 수 있다.")
	public void testProfileImageUrlChange() {
		// given
		String initialUrl = "http://example.com/old_profile.jpg";
		String newUrl = "http://example.com/new_profile.jpg";
		UserProfileImageUrl imageUrl = new UserProfileImageUrl(initialUrl);

		// when
		UserProfileImageUrl updatedImageUrl = imageUrl.change(newUrl);

		// then
		assertThat(updatedImageUrl.getValue()).isNotEqualTo(initialUrl);
		assertThat(updatedImageUrl.getValue()).isEqualTo(newUrl);
	}
}
