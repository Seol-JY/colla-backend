package one.colla.teamspace.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamspaceRepository extends JpaRepository<Teamspace, Long> {
	@Query("SELECT t.id, COUNT(ut) "
		+ "FROM Teamspace t "
		+ "JOIN t.userTeamspaces ut "
		+ "WHERE t.id IN :teamspaceIds "
		+ "GROUP BY t.id")
	List<Object[]> countParticipantsByTeamspaceIds(@Param("teamspaceIds") List<Long> teamspaceIds);
}
