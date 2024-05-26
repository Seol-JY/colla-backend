package one.colla.chat.application.dto.response;

import one.colla.chat.domain.ChatChannel;

public record CreateChatChannelResponse(
	Long chatChannelId
) {
	public static CreateChatChannelResponse from(final ChatChannel chatChannel) {
		return new CreateChatChannelResponse(chatChannel.getId());
	}
}


