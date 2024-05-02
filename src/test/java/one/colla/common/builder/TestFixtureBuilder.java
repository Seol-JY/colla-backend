package one.colla.common.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import one.colla.teamspace.domain.Tag;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

@Component
public class TestFixtureBuilder {

	@Autowired
	private BuilderSupporter bs;

	public User buildUser(final User user) {
		return bs.userRepository().save(user);
	}

	public Teamspace buildTeamspace(final Teamspace teamspace) {
		return bs.teamspaceRepository().save(teamspace);
	}

	public UserTeamspace buildUserTeamspace(final UserTeamspace userTeamspace) {
		return bs.userTeamspaceRepository().save(userTeamspace);
	}

	public Tag buildTag(Tag tag) {
		return bs.tagRepository().save(tag);
	}
}
