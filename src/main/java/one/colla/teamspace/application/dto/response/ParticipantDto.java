package one.colla.teamspace.application.dto.response;

import lombok.Builder;
import one.colla.teamspace.domain.Tag;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

@Builder
public record ParticipantDto(
	Long id,
	String profileImageUrl,
	String username,
	String email,
	String role,
	TagDto tag
) {
	public static ParticipantDto of(User user, UserTeamspace userTeamspace, Tag tag) {
		return ParticipantDto.builder()
			.id(user.getId())
			.profileImageUrl(user.getProfileImageUrlValue())
			.username(user.getUsernameValue())
			.email(user.getEmailValue())
			.role(userTeamspace.getTeamspaceRole().name())
			.tag(TagDto.from(tag))
			.build();
	}
}
