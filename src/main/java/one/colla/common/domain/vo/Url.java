package one.colla.common.domain.vo;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.global.exception.VoException;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Url {
	protected static final String URL_REGEX =
		"^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b"
			+ "(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$";

	protected void validate(final String value) {
		if (value.isBlank()) {
			throw new VoException("url은 공백일 수 없습니다.");
		}
		if (isNotMatchUrlForm(value)) {
			throw new VoException("url 형식이 아닙니다.");
		}
	}

	protected boolean isNotMatchUrlForm(final String value) {
		return !Pattern.matches(URL_REGEX, value);
	}
}
