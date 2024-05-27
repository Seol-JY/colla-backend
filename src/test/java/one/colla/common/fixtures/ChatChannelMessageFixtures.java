package one.colla.common.fixtures;

import java.util.UUID;

import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatType;
import one.colla.user.domain.User;

public class ChatChannelMessageFixtures {
	public static final String CHAT_MESSAGE1_CONTENT = "메시지_1";

	public static ChatChannelMessage RANDOM_CHAT_MESSAGE(User user, ChatChannel chatChannel, ChatType chatType) {
		String randomMessageContent = "메시지_" + UUID.randomUUID().toString().substring(0, 8);
		return ChatChannelMessage.of(user, chatChannel, chatType, randomMessageContent);
	}

	public static ChatChannelMessage CHAT_MESSAGE1(User user, ChatChannel chatChannel, ChatType chatType) {
		return ChatChannelMessage.of(user, chatChannel, chatType, CHAT_MESSAGE1_CONTENT);
	}

}
