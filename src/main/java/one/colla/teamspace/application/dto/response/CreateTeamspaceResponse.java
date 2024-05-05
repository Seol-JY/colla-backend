package one.colla.teamspace.application.dto.response;

import one.colla.teamspace.domain.Teamspace;

public record CreateTeamspaceResponse(
	Long teamspaceId
) {
	public static CreateTeamspaceResponse from(final Teamspace teamspace) {
		final Long teamspaceId = teamspace.getId();
		return new CreateTeamspaceResponse(teamspaceId);
	}
}
