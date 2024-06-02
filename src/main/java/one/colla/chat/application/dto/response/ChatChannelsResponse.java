package one.colla.chat.application.dto.response;

import java.util.List;

public record ChatChannelsResponse(List<ChatChannelInfoDto> chatChannels) {

	public static ChatChannelsResponse from(List<ChatChannelInfoDto> chatChannels) {
		return new ChatChannelsResponse(chatChannels);
	}
}
