package one.colla.chat.application.dto.response;

import java.util.List;

public record ChatChannelMessagesResponse(
	List<ChatChannelMessageResponse> chatChannelMessages
) {
	public static ChatChannelMessagesResponse from(List<ChatChannelMessageResponse> chatChannelMessages) {
		return new ChatChannelMessagesResponse(chatChannelMessages);
	}
}
