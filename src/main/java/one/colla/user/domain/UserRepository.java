package one.colla.user.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import one.colla.user.domain.vo.Email;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(Email email);
}
