package one.colla.user.domain.vo;

import java.util.Objects;

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
public class Username {

	public static final int MIN_LENGTH = 2;
	public static final int MAX_LENGTH = 50;

	@Column(name = "username", nullable = false, length = MAX_LENGTH)
	private String value;

	public Username(final String value) {
		validateNull(value);
		final String trimmedValue = value.trim();
		validate(trimmedValue);
		this.value = trimmedValue;
	}

	private void validateNull(final String value) {
		if (Objects.isNull(value)) {
			throw new VoException("유저 이름은 null일 수 없습니다.");
		}
	}

	private void validate(final String value) {
		if (value.length() > MAX_LENGTH || value.length() < MIN_LENGTH) {
			throw new VoException("닉네임은 2자 이상 50자 이하이어야 합니다.");
		}
		if (value.isBlank()) {
			throw new VoException("닉네임은 공백일 수 없습니다.");
		}
	}

	public Username change(final String username) {
		return new Username(username);
	}

	public static Username from(final String username) {
		return new Username(username);
	}

}
