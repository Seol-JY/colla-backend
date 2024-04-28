package one.colla.teamspace.application.dto.response;

import lombok.Builder;
import one.colla.teamspace.domain.Teamspace;

@Builder
public record TeamspaceInfoResponse(
	Long teamspaceId,
	String teamspaceName,
	String teamspaceProfileImageUrl,
	boolean isParticipated
) {
	public static TeamspaceInfoResponse of(boolean isParticipatedUser, Teamspace teamspace) {
		return TeamspaceInfoResponse.builder()
			.teamspaceId(teamspace.getId())
			.teamspaceName(teamspace.getTeamspaceName().getValue())
			.teamspaceProfileImageUrl(teamspace.getProfileImageUrlValue())
			.isParticipated(isParticipatedUser).build();
	}
}
