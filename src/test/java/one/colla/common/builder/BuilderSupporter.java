package one.colla.common.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import one.colla.user.domain.UserRepository;

@Component
public class BuilderSupporter {

	@Autowired
	private UserRepository userRepository;

	public UserRepository userRepository() {
		return userRepository;
	}
}
