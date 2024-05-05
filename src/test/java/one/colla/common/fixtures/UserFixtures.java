package one.colla.common.fixtures;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.user.domain.Role;
import one.colla.user.domain.User;

public class UserFixtures {

	/**
	 * NAME
	 */
	public static final String USER1_USERNAME = "USER1";
	public static final String USER2_USERNAME = "USER2";

	/**
	 * PASSWORD
	 */
	public static final String USER1_PASSWORD = "USER1_PASSWORD";
	public static final String USER2_PASSWORD = "USER1_PASSWORD";

	/**
	 * EMAIL
	 */
	public static final String USER1_EMAIL = "user1@gmail.com";
	public static final String USER2_EMAIL = "user2@gmail.com";

	/**
	 * ENTITY
	 */
	public static User USER1() {
		return User.createGeneralUser(USER1_USERNAME, USER1_PASSWORD, USER1_EMAIL);
	}

	public static User USER2() {
		return User.createGeneralUser(USER2_USERNAME, USER2_PASSWORD, USER2_EMAIL);
	}

	public static User RANDOMUSER() {
		String randomUsername = "USER_" + UUID.randomUUID().toString().substring(0, 8);
		String randomPassword = "PASSWORD12" + UUID.randomUUID().toString().substring(0, 8);
		String randomEmail = randomUsername + "@example.com";
		return User.createGeneralUser(randomUsername, randomPassword, randomEmail);
	}

	public static CustomUserDetails createCustomUserDetailsByUser(User user) {
		return CustomUserDetails.builder()
			.userId(user.getId())
			.username(user.getUsernameValue())
			.userEmail(user.getEmailValue())
			.authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
			.build();
	}

	public static CustomUserDetails createCustomUserDetailsByUserId(Long userId) {
		return CustomUserDetails.builder()
			.userId(userId)
			.username(USER1_USERNAME)
			.userEmail(USER1_EMAIL)
			.authorities(List.of(new SimpleGrantedAuthority("ROLE_" + Role.USER.name())))
			.build();
	}
}
