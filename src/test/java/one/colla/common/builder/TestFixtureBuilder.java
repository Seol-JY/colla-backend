package one.colla.common.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import one.colla.user.domain.User;

@Component
public class TestFixtureBuilder {

	@Autowired
	private BuilderSupporter bs;

	public User buildUser(final User user) {
		return bs.userRepository().save(user);
	}
}
