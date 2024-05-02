package one.colla.common.fixtures;

import one.colla.teamspace.domain.Teamspace;

public class TeamspaceFixtures {
	public static final String OS_TEAMSPACE_NAME = "운영체제 팀플 과제";
	public static final String DATABASE_TEAMSPACE_NAME = "데이터베이스 텀프로젝트";

	public static Teamspace OS_TEAMSPACE() {
		return Teamspace.from(OS_TEAMSPACE_NAME);
	}

	public static Teamspace DATABASE_TEAMSPACE() {
		return Teamspace.from(DATABASE_TEAMSPACE_NAME);
	}
}
