package one.colla.infra.redis.lastseen;

import java.util.Optional;

public interface LastSeenTeamspaceService {

	/**
	 * 마지막으로 본 팀스페이스 id를 redis 저장한다.
	 *
	 * @param lastSeenTeamspace :  {@link LastSeenTeamspace}
	 */
	void save(LastSeenTeamspace lastSeenTeamspace);

	/**
	 *
	 * @param userId : 조회할 userId
	 * */
	Optional<LastSeenTeamspace> findByUserId(Long userId);

	/**
	 *
	 * @param teamSpaceId : 수정할 유저 Id
	 * @param teamSpaceId : 마지막으로 본 팀스페이스 Id
	 * */
	void updateLastSeenTeamspace(Long userId, Long teamSpaceId);
}
