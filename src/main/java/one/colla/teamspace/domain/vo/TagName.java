package one.colla.teamspace.domain.vo;

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
public class TagName {
	private static final int MIN_LENGTH = 2;
	private static final int MAX_LENGTH = 15;

	@Column(name = "name", nullable = false, length = 50)
	private String value;

	private TagName(final String value) {
		validate(value);
		this.value = value;
	}

	public static TagName from(String tagName) {
		return new TagName(tagName);
	}

	private void validate(final String value) {
		if (Objects.isNull(value)) {
			throw new VoException("태그 이름은 null 일 수 없습니다.");
		}
		if (value.length() > MAX_LENGTH) {
			throw new VoException("태그 이름은" + MAX_LENGTH + "자 이하여야 합니다.");
		}
		if (value.length() < MIN_LENGTH) {
			throw new VoException("태그 이름은" + MIN_LENGTH + "자 이상이여야 합니다.");
		}
	}
}
