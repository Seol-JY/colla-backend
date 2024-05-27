package one.colla.chat.application.dto.response;

import lombok.Builder;
import one.colla.user.domain.User;

@Builder
public record ChatChannelMessageAuthorDto(
	Long id,
	String username,
	String profileImageUrl
) {
	public static ChatChannelMessageAuthorDto from(User author) {
		return ChatChannelMessageAuthorDto.builder()
			.id(author.getId())
			.username(author.getUsernameValue())
			.profileImageUrl(author.getProfileImageUrlValue())
			.build();
	}
}
