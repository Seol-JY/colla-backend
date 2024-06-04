package one.colla.common.domain.vo;

import java.util.regex.Pattern;

import lombok.Getter;
import one.colla.global.exception.VoException;

@Getter
public abstract class Url {
	protected static final String URL_REGEX =
		"^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\."
			+ "[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";

	protected void validate(final String value) {
		if (value == null) {
			throw new VoException("url은 null일 수 없습니다.");
		}
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
