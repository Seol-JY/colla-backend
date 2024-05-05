package one.colla.common.fixtures;

import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.TeamspaceRole;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

public class UserTeamspaceFixtures {
	public static UserTeamspace LEADER_USERTEAMSPACE(User user, Teamspace teamspace) {
		return user.participate(teamspace, TeamspaceRole.LEADER);
	}

	public static UserTeamspace MEMBER_USERTEAMSPACE(User user, Teamspace teamspace) {
		return user.participate(teamspace, TeamspaceRole.MEMBER);
	}
}
