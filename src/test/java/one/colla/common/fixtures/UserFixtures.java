package one.colla.common.fixtures;

import one.colla.user.domain.User;

public class UserFixtures {

	/**
	 * NAME
	 */
	public static final String USER1_USERNAME = "USER1";

	/**
	 * PASSWORD
	 */
	public static final String USER1_PASSWORD = "USER1_PASSWORD";

	/**
	 * EMAIL
	 */
	public static final String USER1_EMAIL = "user1@gmail.com";

	/**
	 * ENTITY
	 */
	public static User USER1() {
		return User.createGeneralUser(USER1_USERNAME, USER1_PASSWORD, USER1_EMAIL);
	}

}
