package one.colla.user.application.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record UserStatusResponse(
	ProfileDto profile,
	List<ParticipatedTeamspaceDto> participatedTeamspaces
) {
	public static UserStatusResponse of(ProfileDto profile, List<ParticipatedTeamspaceDto> participatedTeamspaces) {
		return new UserStatusResponse(profile, participatedTeamspaces);
	}
}
