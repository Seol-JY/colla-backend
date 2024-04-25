package one.colla.user.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

public class UsernameTest {

	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	public void testEqualsAndHashCode1() {
		// given
		String input = "Nick";

		//when
		Username username1 = new Username(input);
		Username username2 = new Username(input);

		//then
		assertThat(username1).isEqualTo(username2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	public void testEqualsAndHashCode2() {
		// given
		String input1 = "Nick";
		String input2 = "Name";

		// when
		Username username1 = new Username(input1);
		Username username2 = new Username(input2);

		// then
		assertThat(username1).isNotEqualTo(username2);
	}

	@Test
	@DisplayName("유효한 닉네임을 생성할 수 있다.")
	public void testValidUsername() {
		// given
		String input = "ValidUsername";

		// when
		Username username = new Username(input);

		// then
		assertThat(username.getValue()).isEqualTo(input);
	}

	@Test
	@DisplayName("닉네임 글자 수는 2자 미만일 수 없다.")
	public void testUsernameTooShort() {
		// given
		String input = "S";

		// when  & then
		assertThatThrownBy(() -> new Username(input))
			.isInstanceOf(VoException.class)
			.hasMessage("닉네임은 2자 이상 50자 이하이어야 합니다.");
	}

	@Test
	@DisplayName("닉네임 글자 수는 50자 이상일 수 없다.")
	public void testUsernameTooLong() {
		// given
		String input = "L".repeat(Username.MAX_LENGTH + 1);

		// when & then
		assertThatThrownBy(() -> new Username(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("닉네임은 2자 이상 50자 이하이어야 합니다.");
	}

	@Test
	@DisplayName("닉네임은 null이 될 수 없다.")
	public void testUsernameNull() {
		// given
		String input = null;

		// when & then
		assertThatThrownBy(() -> new Username(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("유저 이름은 null일 수 없습니다.");
	}

	@Test
	@DisplayName("닉네임은 공백일 수 없다.")
	public void testUsernameBlank() {
		// given
		String input = "   ";

		// when & then
		assertThatThrownBy(() -> new Username(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("닉네임은 공백일 수 없습니다.");
	}
}
