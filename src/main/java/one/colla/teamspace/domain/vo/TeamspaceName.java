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
public class TeamspaceName {
	private static final int MIN_LENGTH = 2;
	private static final int MAX_LENGTH = 20;

	@Column(name = "name", nullable = false, length = MAX_LENGTH)
	private String value;

	private TeamspaceName(final String value) {
		validate(value);
		this.value = value;
	}

	public static TeamspaceName from(String teamspaceName) {
		return new TeamspaceName(teamspaceName);
	}

	private void validate(final String value) {
		if (Objects.isNull(value)) {
			throw new VoException("팀스페이스 이름은 null 일 수 없습니다.");
		}
		if (value.isBlank()) {
			throw new VoException("팀스페이스 이름은 공백일 수 없습니다.");
		}
		if (value.length() > MAX_LENGTH || value.length() < MIN_LENGTH) {
			throw new VoException("팀스페이스 이름은 " + MIN_LENGTH + "자 이상, " + MAX_LENGTH + "자 이하여야 합니다.");
		}
	}
}
