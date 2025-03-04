package one.colla.user.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import one.colla.user.domain.vo.Email;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(Email email);

	@Query("SELECT DISTINCT u FROM User u "
		+ "LEFT JOIN FETCH u.userTeamspaces ut "
		+ "LEFT JOIN FETCH ut.teamspace t "
		+ "WHERE u.id = :userId")
	Optional<User> findByIdWithTeamspaces(@Param("userId") Long userId);
}
