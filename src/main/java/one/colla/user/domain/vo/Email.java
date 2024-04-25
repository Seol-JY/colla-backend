package one.colla.user.domain.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.global.exception.VoException;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
@Slf4j
public class Email {

	private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

	@Column(name = "email", nullable = false)
	private String value;

	public Email(final String value) {
		validateNull(value);
		validate(value);
		this.value = value;
	}

	private void validateNull(final String value) {
		if (Objects.isNull(value)) {
			throw new VoException("이메일은 null일 수 없습니다.");
		}
	}

	private void validate(final String value) {
		if (value.isBlank()) {
			throw new VoException("이메일은 공백일 수 없습니다.");
		}
		if (isNotMatchEmailForm(value)) {
			log.error("이메일 형식 X");
			throw new VoException("이메일 형식이 아닙니다.");
		}
	}

	private boolean isNotMatchEmailForm(final String value) {
		return !Pattern.matches(EMAIL_REGEX, value);
	}

	public Email change(final String newEmail) {
		return new Email(newEmail);
	}

	public static Email from(final String email) {
		return new Email(email);
	}
}
