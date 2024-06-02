package one.colla.chat.domain.vo;

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
public class ChatChannelMessageContent {
	private static final int MAX_LENGTH = 1024;

	@Column(name = "content", length = 1024)
	private String value;

	private ChatChannelMessageContent(final String value) {
		validate(value);
		this.value = value;
	}

	public static ChatChannelMessageContent from(String chatChannelMessageContent) {
		return new ChatChannelMessageContent(chatChannelMessageContent);
	}

	private void validate(final String value) {
		if (value.length() > MAX_LENGTH) {
			throw new VoException("채팅 메시지는 " + MAX_LENGTH + "자 이하여야 합니다.");
		}
	}
}
