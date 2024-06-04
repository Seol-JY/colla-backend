package one.colla.file.application.dto.response;

import lombok.Builder;
import one.colla.user.domain.User;

@Builder
public record AttachmentAuthorDto(
	Long id,
	String username,
	String profileImageUrl
) {
	public static AttachmentAuthorDto from(User user) {
		return AttachmentAuthorDto.builder()
			.id(user.getId())
			.username(user.getUsernameValue())
			.profileImageUrl(user.getProfileImageUrlValue())
			.build();
	}
}
