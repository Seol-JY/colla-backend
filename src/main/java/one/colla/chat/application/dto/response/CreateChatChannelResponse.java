package one.colla.chat.application.dto.response;

import one.colla.chat.domain.ChatChannel;

public record CreateChatChannelResponse(
	Long chatChannelId
) {
	public static CreateChatChannelResponse from(final ChatChannel chatChannel) {
		final Long chatChannelId = chatChannel.getId();
		return new CreateChatChannelResponse(chatChannelId);
	}
}


