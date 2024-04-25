package one.colla.common.builder.fixtures;

import one.colla.user.domain.User;

public class UserFixtures {

	/**
	 * NAME
	 */
	public static final String USER_1_USERNAME = "USER1";

	/**
	 * PASSWORD
	 */
	public static final String USER_1_PASSWORD = "USER1_PASSWORD";

	/**
	 * EMAIL
	 */
	public static final String USER_1_EMAIL = "user1@gmail.com";

	/**
	 * ENTITY
	 */
	public static User user1() {
		return User.createGeneralUser(USER_1_USERNAME, USER_1_PASSWORD, USER_1_EMAIL);
	}

}
