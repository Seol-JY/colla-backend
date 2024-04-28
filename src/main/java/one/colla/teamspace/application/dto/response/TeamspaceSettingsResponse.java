package one.colla.teamspace.application.dto.response;

import java.util.List;

import one.colla.teamspace.domain.Teamspace;

public record TeamspaceSettingsResponse(
	String profileImageUrl,
	String name,
	List<TagDto> tags,
	List<ParticipantDto> users
) {
	public static TeamspaceSettingsResponse of(Teamspace teamspace, List<ParticipantDto> participants) {
		List<TagDto> tagDtos = teamspace.getTags().stream().map(TagDto::from).toList();
		return new TeamspaceSettingsResponse(
			teamspace.getProfileImageUrl(),
			teamspace.getNameValue(),
			tagDtos,
			participants
		);
	}
}
