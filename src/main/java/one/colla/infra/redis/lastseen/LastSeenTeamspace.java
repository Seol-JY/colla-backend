package one.colla.infra.redis.lastseen;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@RedisHash("lastSeenTeamspace")
@Getter
@ToString(of = {"userId", "teamspaceId"})
@EqualsAndHashCode(of = {"userId", "teamspaceId"})
public class LastSeenTeamspace {
	@Id
	private final Long userId;
	private Long teamspaceId;

	@Builder
	private LastSeenTeamspace(Long userId, Long teamspaceId) {
		this.userId = userId;
		this.teamspaceId = teamspaceId;
	}

	public static LastSeenTeamspace of(Long userId, Long teamspaceId) {
		return LastSeenTeamspace.builder()
			.userId(userId)
			.teamspaceId(teamspaceId)
			.build();
	}

	protected void updateLastSeenTeamspace(Long teamspaceId) {
		this.teamspaceId = teamspaceId;
	}
}
