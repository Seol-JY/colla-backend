package one.colla.file.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

class FileUrlTest {

	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	void testEqualsAndHashCode1() {
		// given
		String input = "https://cdn.colla.so/profile.jpg";

		// when
		FileUrl url1 = new FileUrl(input);
		FileUrl url2 = new FileUrl(input);

		// then
		assertThat(url1).isEqualTo(url2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	void testEqualsAndHashCode2() {
		// given
		String input1 = "https://cdn.colla.so/profile.jpg";
		String input2 = "https://cdn.colla.so/another.jpg";

		// when
		FileUrl url1 = new FileUrl(input1);
		FileUrl url2 = new FileUrl(input2);

		// then
		assertThat(url1).isNotEqualTo(url2);
	}

	@Test
	@DisplayName("유효한 파일 URL을 생성할 수 있다.")
	void testValidFileUrl() {
		// given
		String validUrl = "https://cdn.colla.so/한글.jpg";

		// when
		FileUrl fileUrl = new FileUrl(validUrl);

		// then
		assertThat(fileUrl.getValue()).isEqualTo(validUrl);
	}

	@Test
	@DisplayName("유효하지 않은 형식의 URL이면 예외가 발생한다.")
	void testInvalidFileUrlFormat() {
		// given
		String invalidUrl = "htp:/example.com";

		// when/then
		assertThatThrownBy(() -> new FileUrl(invalidUrl))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("url 형식이 아닙니다.");
	}

	@Test
	@DisplayName("허용되지 않은 프리픽스의 URL이면 예외가 발생한다.")
	void testNotAllowedFileUrlPrefix() {
		// given
		String invalidUrl = "https://example.com/file.jpg";

		// when/then
		assertThatThrownBy(() -> new FileUrl(invalidUrl))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("CDN Url 이 아닙니다.");
	}

	@Test
	@DisplayName("URL은 공백일 수 없다.")
	void testBlankFileUrl() {
		// given
		String blankUrl = "   ";

		// when/then
		assertThatThrownBy(() -> new FileUrl(blankUrl))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("url은 공백일 수 없습니다.");
	}

	@Test
	@DisplayName("파일 URL을 변경할 수 있다.")
	void testFileUrlChange() {
		// given
		String initialUrl = "https://cdn.colla.so/old_file.jpg";
		String newUrl = "https://cdn.colla.so/new_file.jpg";
		FileUrl fileUrl = new FileUrl(initialUrl);

		// when
		FileUrl updatedFileUrl = fileUrl.change(newUrl);

		// then
		assertThat(updatedFileUrl.getValue()).isNotEqualTo(initialUrl);
		assertThat(updatedFileUrl.getValue()).isEqualTo(newUrl);
	}

	@Test
	@DisplayName("URL이 null이면 예외가 발생한다.")
	void testNullFileUrl() {
		// given
		String nullUrl = null;

		// when/then
		assertThatThrownBy(() -> new FileUrl(nullUrl))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("url은 null일 수 없습니다.");
	}
}
