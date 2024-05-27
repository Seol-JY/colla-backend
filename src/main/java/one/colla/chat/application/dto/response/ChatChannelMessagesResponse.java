package one.colla.chat.application.dto.response;

import java.util.List;

public record ChatChannelMessagesResponse(
	List<ChatChannelMessageInfoDto> chatChannelMessages
) {
	public static ChatChannelMessagesResponse from(List<ChatChannelMessageInfoDto> chatChannelMessages) {
		return new ChatChannelMessagesResponse(chatChannelMessages);
	}
}
