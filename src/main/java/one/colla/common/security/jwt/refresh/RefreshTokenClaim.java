package one.colla.common.security.jwt.refresh;

import java.util.Map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import one.colla.common.security.jwt.JwtClaims;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenClaim implements JwtClaims {
	private final Map<String, ?> claims;

	public static RefreshTokenClaim of(Long userId, String role) {
		Map<String, Object> claims = Map.of(
			RefreshTokenClaimKeys.USER_ID.getValue(), userId.toString(),
			RefreshTokenClaimKeys.ROLE.getValue(), role
		);
		return new RefreshTokenClaim(claims);
	}

	@Override
	public Map<String, ?> getClaims() {
		return claims;
	}
}
