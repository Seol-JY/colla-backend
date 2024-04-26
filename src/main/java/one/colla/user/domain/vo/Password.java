package one.colla.user.domain.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.global.exception.VoException;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class Password {

	public static final int MIN_LENGTH = 8;
	public static final int MAX_LENGTH = 255;
	private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).*$";

	@Column(name = "password")
	private String value;

	public Password(final String value) {
		validateNull(value);
		validate(value);
		this.value = value;
	}

	private void validateNull(final String value) {
		if (Objects.isNull(value)) {
			throw new VoException("비밀번호는 null일 수 없습니다.");
		}
	}

	private void validate(final String value) {
		if (value.length() > MAX_LENGTH || value.length() < MIN_LENGTH) {
			throw new VoException("비밀번호는 8자 이상 255자 이하이어야 합니다.");
		}
		if (isNotMatchPasswordForm(value)) {
			throw new VoException("비밀번호는 영문자와 숫자를 포함해야 합니다.");
		}
	}

	private boolean isNotMatchPasswordForm(final String value) {
		return !Pattern.matches(PASSWORD_REGEX, value);
	}

	public Password change(final String newPassword) {
		return new Password(newPassword);
	}

	public static Password from(final String password) {
		return new Password(password);
	}
}
