package one.colla.common.fixtures;

import one.colla.chat.domain.ChatChannel;
import one.colla.teamspace.domain.Teamspace;

public class ChatChannelFixtures {
	public static final String FRONTEND_CHAT_CHANNEL_NAME = "프론트엔드 채팅 채널";
	public static final String BACKEND_CHAT_CHANNEL_NAME = "백엔드 채팅 채널";

	public static ChatChannel FRONTEND_CHAT_CHANNEL(Teamspace teamspace) {
		return ChatChannel.of(teamspace, FRONTEND_CHAT_CHANNEL_NAME);
	}

	public static ChatChannel BACKEND_CHAT_CHANNEL(Teamspace teamspace) {
		return ChatChannel.of(teamspace, BACKEND_CHAT_CHANNEL_NAME);
	}
}
