package one.colla.user.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}
}
