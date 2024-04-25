package one.colla.user.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

public class PasswordTest {

	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	public void testEqualsAndHashCode1() {
		// given
		String input = "Password123";

		// when
		Password password1 = new Password(input);
		Password password2 = new Password(input);

		// then
		assertThat(password1).isEqualTo(password2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	public void testEqualsAndHashCode2() {
		// given
		String input1 = "Password123";
		String input2 = "Different456";

		// when
		Password password1 = new Password(input1);
		Password password2 = new Password(input2);

		// then
		assertThat(password1).isNotEqualTo(password2);
	}

	@Test
	@DisplayName("유효한 비밀번호를 생성할 수 있다.")
	public void testValidPassword() {
		// given
		String input = "ValidPassword123";

		// when
		Password password = new Password(input);

		// then
		assertThat(password.getValue()).isEqualTo(input);
	}

	@Test
	@DisplayName("비밀번호 글자 수는 8자 미만일 수 없다.")
	public void testPasswordTooShort() {
		// given
		String input = "Pass1";

		// when/then
		assertThatThrownBy(() -> new Password(input))
			.isInstanceOf(VoException.class)
			.hasMessage("비밀번호는 8자 이상 255자 이하이어야 합니다.");
	}

	@Test
	@DisplayName("비밀번호 글자 수는 255자 이상일 수 없다.")
	public void testPasswordTooLong() {
		// given
		String input = "P".repeat(Password.MAX_LENGTH + 1);

		// when/then
		assertThatThrownBy(() -> new Password(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("비밀번호는 8자 이상 255자 이하이어야 합니다.");
	}

	@Test
	@DisplayName("비밀번호는 null이 될 수 없다.")
	public void testPasswordNull() {
		// given
		String input = null;

		// when/then
		assertThatThrownBy(() -> new Password(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("비밀번호는 null일 수 없습니다.");
	}

	@Test
	@DisplayName("비밀번호는 영문자와 숫자를 포함해야 한다.")
	public void testPasswordInvalidForm() {
		// given
		String input = "password";  // Only letters, no numbers

		// when/then
		assertThatThrownBy(() -> new Password(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("비밀번호는 영문자와 숫자를 포함해야 합니다.");
	}
}
