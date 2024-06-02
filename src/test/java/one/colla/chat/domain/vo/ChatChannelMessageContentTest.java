package one.colla.chat.domain.vo;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

class ChatChannelMessageContentTest {

	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	void testEqualsAndHashCode1() {
		// given
		String input = "Hello, World!";

		// when
		ChatChannelMessageContent content1 = ChatChannelMessageContent.from(input);
		ChatChannelMessageContent content2 = ChatChannelMessageContent.from(input);

		// then
		assertThat(content1).isEqualTo(content2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	void testEqualsAndHashCode2() {
		// given
		String input1 = "Hello, World!";
		String input2 = "Different content";

		// when
		ChatChannelMessageContent content1 = ChatChannelMessageContent.from(input1);
		ChatChannelMessageContent content2 = ChatChannelMessageContent.from(input2);

		// then
		assertThat(content1).isNotEqualTo(content2);
	}

	@Test
	@DisplayName("유효한 채팅 메시지를 생성할 수 있다.")
	void testValidChatChannelMessageContent() {
		// given
		String input = "Valid chat message";

		// when
		ChatChannelMessageContent content = ChatChannelMessageContent.from(input);

		// then
		assertThat(content.getValue()).isEqualTo(input);
	}

	@Test
	@DisplayName("채팅 메시지 글자 수는 1024자 초과일 수 없다.")
	void testChatChannelMessageContentTooLong() {
		// given
		String input = "A".repeat(1024 + 1);

		// when & then
		assertThatThrownBy(() -> ChatChannelMessageContent.from(input))
			.isInstanceOf(VoException.class)
			.hasMessage("채팅 메시지는 1024자 이하여야 합니다.");
	}
}
