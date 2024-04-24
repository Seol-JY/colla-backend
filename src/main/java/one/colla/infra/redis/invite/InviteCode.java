package one.colla.infra.redis.invite;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(of = {"code", "teamspaceId", "ttl"})
@EqualsAndHashCode(of = {"code", "teamspaceId"})
@RedisHash("inviteCode")
public class InviteCode {
	@Id
	private final String code;
	private final long teamspaceId;
	@TimeToLive
	private final long ttl;

	@Builder
	private InviteCode(String code, long teamspaceId, long ttl) {
		this.code = code;
		this.teamspaceId = teamspaceId;
		this.ttl = ttl;
	}

	public static InviteCode of(String code, long teamspaceId, long ttl) {
		return new InviteCode(code, teamspaceId, ttl);
	}
}
