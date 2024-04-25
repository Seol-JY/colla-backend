package one.colla.infra.redis.verify;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@RedisHash("verifyCode")
@Getter
@ToString(of = {"email", "verifyCode", "ttl"})
@EqualsAndHashCode(of = {"email", "verifyCode"})
public class VerifyCode {

	@Id
	private final String email;
	private final String verifyCode;
	private final long ttl;

	@Builder
	private VerifyCode(String email, String verifyCode, long ttl) {
		this.email = email;
		this.verifyCode = verifyCode;
		this.ttl = ttl;
	}

	public static VerifyCode of(String email, String verifyCode, long ttl) {
		return VerifyCode.builder()
			.email(email)
			.verifyCode(verifyCode)
			.ttl(ttl)
			.build();
	}
}
