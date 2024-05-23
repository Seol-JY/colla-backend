package one.colla.chat.domain.vo;

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
public class ChatChannelName {
	private static final int MIN_LENGTH = 1;
	private static final int MAX_LENGTH = 15;

	@Column(name = "name", nullable = false, length = 50)
	private String value;

	private ChatChannelName(final String value) {
		validate(value);
		this.value = value;
	}

	public static ChatChannelName from(String chatChannelName) {
		return new ChatChannelName(chatChannelName);
	}

	private void validate(final String value) {
		if (Objects.isNull(value)) {
			throw new VoException("채팅 채널 이름은 null 일 수 없습니다.");
		}
		if (value.isEmpty()) {
			throw new VoException("채팅 채널 이름은 " + MIN_LENGTH + "자 이상이어야 합니다.");
		}
		if (value.isBlank()) {
			throw new VoException("채팅 채널 이름은 공백일 수 없습니다.");
		}
		if (value.length() > MAX_LENGTH) {
			throw new VoException("채팅 채널 이름은 " + MIN_LENGTH + "자 이상, " + MAX_LENGTH + "자 이하여야 합니다.");
		}
	}
}
