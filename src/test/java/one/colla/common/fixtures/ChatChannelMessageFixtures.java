package one.colla.common.fixtures;

import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatType;
import one.colla.user.domain.User;

public class ChatChannelMessageFixtures {
	public static final String CHAT_MESSAGE1_CONTENT = "메시지1 내용";

	public static ChatChannelMessage CHAT_MESSAGE1(User user, ChatChannel chatChannel, ChatType chatType) {
		return ChatChannelMessage.of(user, chatChannel, chatType, CHAT_MESSAGE1_CONTENT);
	}

}
