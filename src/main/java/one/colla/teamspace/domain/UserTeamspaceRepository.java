package one.colla.teamspace.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import one.colla.user.domain.User;

public interface UserTeamspaceRepository extends JpaRepository<UserTeamspace, Long> {
	boolean existsByUserAndTeamspace(User user, Teamspace teamspace);
}
