package one.colla.teamspace.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import one.colla.teamspace.domain.vo.TagName;

public interface TagRepository extends JpaRepository<Tag, Long> {
	boolean existsByTeamspaceAndTagName(Teamspace teamspace, TagName tagName);
}
