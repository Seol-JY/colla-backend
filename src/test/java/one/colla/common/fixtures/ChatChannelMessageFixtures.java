package one.colla.common.fixtures;

import java.util.UUID;

import one.colla.chat.application.dto.request.ChatCreateRequest;
import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatType;
import one.colla.teamspace.domain.Teamspace;
import one.colla.user.domain.User;

public class ChatChannelMessageFixtures {
	public static final String CHAT_MESSAGE1_CONTENT = "메시지_1";

	public static ChatChannelMessage RANDOM_CHAT_MESSAGE(User user, Teamspace teamspace, ChatChannel chatChannel) {
		String randomMessageContent = "메시지_" + UUID.randomUUID().toString().substring(0, 8);
		ChatCreateRequest chatCreateRequest = new ChatCreateRequest(ChatType.TEXT, randomMessageContent, null, null);
		return ChatChannelMessage.of(user, teamspace, chatChannel, chatCreateRequest);
	}

	public static ChatChannelMessage CHAT_MESSAGE1(User user, Teamspace teamspace, ChatChannel chatChannel) {
		ChatCreateRequest chatCreateRequest = new ChatCreateRequest(ChatType.TEXT, CHAT_MESSAGE1_CONTENT, null, null);
		return ChatChannelMessage.of(user, teamspace, chatChannel, chatCreateRequest);
	}

}
