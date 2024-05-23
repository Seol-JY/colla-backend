package one.colla.chat.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

class ChatChannelNameTest {

	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	void testEqualsAndHashCode1() {
		// given
		String input = "channelName";

		// when
		ChatChannelName chatChannelName1 = ChatChannelName.from(input);
		ChatChannelName chatChannelName2 = ChatChannelName.from(input);

		// then
		assertThat(chatChannelName1).isEqualTo(chatChannelName2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	void testEqualsAndHashCode2() {
		// given
		String input1 = "channelName1";
		String input2 = "channelName2";

		// when
		ChatChannelName chatChannelName1 = ChatChannelName.from(input1);
		ChatChannelName chatChannelName2 = ChatChannelName.from(input2);

		// then
		assertThat(chatChannelName1).isNotEqualTo(chatChannelName2);
	}

	@Test
	@DisplayName("유효한 채널 이름을 생성할 수 있다.")
	void testValidChatChannelName() {
		// given
		String input = "ValidChannel";

		// when
		ChatChannelName chatChannelName = ChatChannelName.from(input);

		// then
		assertThat(chatChannelName.getValue()).isEqualTo(input);
	}

	@Test
	@DisplayName("채널 이름 글자 수는 1자 미만일 수 없다.")
	void testChatChannelNameTooShort() {
		// given
		String input = "";

		// when & then
		assertThatThrownBy(() -> ChatChannelName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessage("채팅 채널 이름은 1자 이상이어야 합니다.");
	}

	@Test
	@DisplayName("채널 이름 글자 수는 15자 초과일 수 없다.")
	void testChatChannelNameTooLong() {
		// given
		String input = "L".repeat(15 + 1);

		// when & then
		assertThatThrownBy(() -> ChatChannelName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("채팅 채널 이름은 1자 이상, 15자 이하여야 합니다.");
	}

	@Test
	@DisplayName("채널 이름은 null이 될 수 없다.")
	void testChatChannelNameNull() {
		// given
		String input = null;

		// when & then
		assertThatThrownBy(() -> ChatChannelName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("채팅 채널 이름은 null 일 수 없습니다.");
	}

	@Test
	@DisplayName("채널 이름은 공백일 수 없다.")
	void testChatChannelNameBlank() {
		// given
		String input = "   ";

		// when & then
		assertThatThrownBy(() -> ChatChannelName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("채팅 채널 이름은 공백일 수 없습니다.");
	}
}
