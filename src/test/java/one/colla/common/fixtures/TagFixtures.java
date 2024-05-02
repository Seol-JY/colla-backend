package one.colla.common.fixtures;

import one.colla.teamspace.domain.Tag;
import one.colla.teamspace.domain.Teamspace;

public class TagFixtures {
	public static final String FRONTEND_TAG_NAME = "프론트엔드";
	public static final String BACKEND_TAG_NAME = "백엔드";

	public static Tag FRONTEND_TAG(Teamspace teamspace) {
		return Tag.of(FRONTEND_TAG_NAME, teamspace);
	}

	public static Tag BACKEND_TAG(Teamspace teamspace) {
		return Tag.of(BACKEND_TAG_NAME, teamspace);
	}
}
