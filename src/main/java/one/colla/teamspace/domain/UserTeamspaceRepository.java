package one.colla.teamspace.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import one.colla.user.domain.User;

public interface UserTeamspaceRepository extends JpaRepository<UserTeamspace, Long> {
	boolean existsByUserAndTeamspace(User user, Teamspace teamspace);

	Optional<UserTeamspace> findByUserIdAndTeamspaceId(Long userId, Long teamspaceId);

	List<UserTeamspace> findAllByTeamspace(Teamspace teamspace);

}
