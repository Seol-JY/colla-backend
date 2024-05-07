package one.colla.common.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import one.colla.teamspace.domain.TagRepository;
import one.colla.teamspace.domain.TeamspaceRepository;
import one.colla.teamspace.domain.UserTeamspaceRepository;
import one.colla.user.domain.UserRepository;

@Component
public class BuilderSupporter {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TeamspaceRepository teamspaceRepository;

	@Autowired
	private UserTeamspaceRepository userTeamspaceRepository;

	@Autowired
	private TagRepository tagRepository;

	public UserRepository userRepository() {
		return userRepository;
	}

	public TeamspaceRepository teamspaceRepository() {
		return teamspaceRepository;
	}

	public UserTeamspaceRepository userTeamspaceRepository() {
		return userTeamspaceRepository;
	}

	public TagRepository tagRepository() {
		return tagRepository;
	}
}