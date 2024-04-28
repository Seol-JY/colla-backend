package one.colla.teamspace.application.dto.response;

import java.util.List;

public record TeamspaceParticipantsResponse(List<ParticipantDto> users) {

	public static TeamspaceParticipantsResponse from(List<ParticipantDto> participants) {
		return new TeamspaceParticipantsResponse(participants);
	}
}
