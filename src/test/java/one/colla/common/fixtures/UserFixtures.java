package one.colla.common.fixtures;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.user.domain.Role;
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

	public static CustomUserDetails createCustomUserDetails(Long userId) {
		return CustomUserDetails.builder()
			.userId(userId)
			.username(USER1_USERNAME)
			.userEmail(USER1_EMAIL)
			.authorities(List.of(new SimpleGrantedAuthority("ROLE_" + Role.USER.name())))
			.build();
	}
}
