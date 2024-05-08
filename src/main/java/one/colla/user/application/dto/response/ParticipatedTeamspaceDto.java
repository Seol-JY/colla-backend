package one.colla.user.application.dto.response;

import lombok.Builder;
import one.colla.teamspace.domain.TeamspaceRole;
import one.colla.teamspace.domain.UserTeamspace;

@Builder
public record ParticipatedTeamspaceDto(
	Long teamspaceId,
	String name,
	String profileImageUrl,
	TeamspaceRole teamspaceRole,
	int numOfParticipants
) {
	public static ParticipatedTeamspaceDto of(
		Long teamspaceId,
		UserTeamspace userTeamspace,
		int numOfTeamspaceParticipants) {
		return ParticipatedTeamspaceDto.builder()
			.teamspaceId(teamspaceId)
			.name(userTeamspace.getTeamspace().getTeamspaceNameValue())
			.profileImageUrl(userTeamspace.getTeamspace().getProfileImageUrlValue())
			.teamspaceRole(userTeamspace.getTeamspaceRole())
			.numOfParticipants(numOfTeamspaceParticipants)
			.build();

	}
}
