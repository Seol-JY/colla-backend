package one.colla.user.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

public class EmailTest {

	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	public void testEqualsAndHashCode1() {
		// given
		String input = "user@example.com";

		// when
		Email email1 = new Email(input);
		Email email2 = new Email(input);

		// then
		assertThat(email1).isEqualTo(email2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	public void testEqualsAndHashCode2() {
		// given
		String input1 = "user@example.com";
		String input2 = "another@example.com";

		// when
		Email email1 = new Email(input1);
		Email email2 = new Email(input2);

		// then
		assertThat(email1).isNotEqualTo(email2);
	}

	@Test
	@DisplayName("유효한 이메일을 생성할 수 있다.")
	public void testValidEmail() {
		// given
		String input = "valid.email@example.com";

		// when
		Email email = new Email(input);

		// then
		assertThat(email.getValue()).isEqualTo(input);
	}

	@Test
	@DisplayName("이메일은 null이 될 수 없다.")
	public void testEmailNull() {
		// given
		String input = null;

		// when/then
		assertThatThrownBy(() -> new Email(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("이메일은 null일 수 없습니다.");
	}

	@Test
	@DisplayName("이메일은 공백일 수 없다.")
	public void testEmailBlank() {
		// given
		String input = "  ";

		// when/then
		assertThatThrownBy(() -> new Email(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("이메일은 공백일 수 없습니다.");
	}

	@Test
	@DisplayName("이메일 형식이 유효해야 한다.")
	public void testEmailInvalidForm() {
		String[] invalidEmails = {
			"plainaddress",         // '@' 기호와 도메인이 없음
			".username@yahoo.com",  // 주소의 시작 부분에 점이 있어서는 안 됨
			"username@yahoo.com.",  // 주소의 끝 부분에 점이 있어서는 안 됨
			"username@yahoo..com",  // 연속된 점이 있음
			"username@.com",        // 도메인 부분이 점으로 시작됨
			"@yahoo.com",           // 사용자 이름이 누락됨
			"username@domain.c",    // 최상위 도메인(TLD)이 한 글자임
			"username@domain.corporate" // 최상위 도메인(TLD)이 너무 김
		};

		// when/then
		for (String email : invalidEmails) {
			assertThatThrownBy(() -> new Email(email))
				.isInstanceOf(VoException.class)
				.hasMessageContaining("이메일 형식이 아닙니다.");
		}
	}
}
