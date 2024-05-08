package one.colla.user.application.dto.response;

import lombok.Builder;
import one.colla.user.domain.CommentNotification;
import one.colla.user.domain.User;

@Builder
public record ProfileDto(
	Long userId,
	String username,
	String profileImageUrl,
	String email,
	boolean emailSubscription,
	CommentNotification commentNotification,
	Long lastSeenTeamspaceId
) {

	public static ProfileDto of(Long userID, User user, Long lastSeenTeamspaceId) {
		return ProfileDto.builder()
			.userId(userID)
			.username(user.getUsernameValue())
			.profileImageUrl(user.getProfileImageUrlValue())
			.email(user.getEmailValue())
			.emailSubscription(user.isEmailSubscription())
			.commentNotification(user.getCommentNotification())
			.lastSeenTeamspaceId(lastSeenTeamspaceId)
			.build();
	}
}
