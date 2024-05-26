package one.colla.feed.common.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.annotation.Nullable;
import one.colla.teamspace.domain.Teamspace;

public interface FeedRepository extends JpaRepository<Feed, Long> {

	Optional<Feed> findByIdAndTeamspace(Long feedId, Teamspace teamspace);

	/**
	 * 주어진 팀스페이스와 기준에 따라 피드를 찾습니다.
	 * 결과는 생성 날짜 기준 내림차순으로 정렬됩니다.
	 *
	 * @param teamspace 피드를 필터링할 팀스페이스
	 * @param after 선택적 피드 ID로, 이 피드의 생성 날짜 이전에 생성된 피드들을 필터링합니다
	 * @param feedClass 선택적 피드 클래스로, 타입별로 피드를 필터링합니다
	 * @param pageable 페이징 정보
	 * @return 주어진 기준에 맞는 피드 목록
	 */
	@Query("SELECT f FROM Feed f WHERE f.teamspace = :teamspace "
		+ "AND (:after IS NULL OR f.createdAt < (SELECT subf.createdAt FROM Feed subf WHERE subf.id = :after)) "
		+ "AND (:feedClass IS NULL OR TYPE(f) = :feedClass) ORDER BY f.createdAt DESC"
	)
	List<Feed> findFeedsByTeamspaceAndCriteria(
		@Param("teamspace") Teamspace teamspace,
		@Param("after") @Nullable Long after,
		@Param("feedClass") @Nullable Class<? extends Feed> feedClass,
		Pageable pageable
	);
}
