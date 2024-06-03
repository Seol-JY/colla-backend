package one.colla.chat.application.dto.response;

import java.util.List;

public record ChatChannelStatusResponse(
	List<ChatChannelInfoDto> chatChannelsResponse
) {
	public static ChatChannelStatusResponse from(List<ChatChannelInfoDto> chatChannelsResponse) {
		return new ChatChannelStatusResponse(chatChannelsResponse);
	}
}
