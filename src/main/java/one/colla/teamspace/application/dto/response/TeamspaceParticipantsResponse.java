package one.colla.teamspace.application.dto.response;

import java.util.List;

import lombok.Builder;
import one.colla.teamspace.domain.Tag;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

public record TeamspaceParticipantsResponse(List<Participant> users) {
	@Builder
	public record Participant(
		Long id,
		String profileImageUrl,
		String username,
		String email,
		String role,
		TagInfo tag
	) {
		public static Participant of(User user, UserTeamspace userTeamspace, Tag tag) {
			TagInfo tagInfo = tag != null ? new TagInfo(tag.getId(), tag.getName()) : null;

			return Participant.builder()
				.id(user.getId())
				.profileImageUrl(user.getProfileImageUrl().getValue())
				.username(user.getUsernameValue())
				.email(user.getEmailValue())
				.role(userTeamspace.getTeamspaceRole().name())
				.tag(tagInfo)
				.build();
		}
	}

	public record TagInfo(
		Long id,
		String name
	) {
	}

	public static TeamspaceParticipantsResponse from(List<Participant> participants) {
		return new TeamspaceParticipantsResponse(participants);
	}
}
