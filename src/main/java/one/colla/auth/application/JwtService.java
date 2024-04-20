package one.colla.auth.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.auth.application.dto.JwtPair;
import one.colla.common.redis.forbidden.ForbiddenTokenService;
import one.colla.common.redis.refresh.RefreshToken;
import one.colla.common.redis.refresh.RefreshTokenService;
import one.colla.common.security.jwt.JwtClaims;
import one.colla.common.security.jwt.JwtProvider;
import one.colla.common.security.jwt.access.AccessTokenClaim;
import one.colla.common.security.jwt.refresh.RefreshTokenClaim;
import one.colla.common.security.jwt.refresh.RefreshTokenClaimKeys;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtProvider accessTokenProvider;
	private final JwtProvider refreshTokenProvider;
	private final RefreshTokenService refreshTokenService;
	private final ForbiddenTokenService forbiddenTokenService;

	public JwtPair createToken(User user) {
		String accessToken = accessTokenProvider.generateToken(
			AccessTokenClaim.of(user.getId(), user.getRole().name()));
		String refreshToken = refreshTokenProvider.generateToken(
			RefreshTokenClaim.of(user.getId(), user.getRole().name()));

		refreshTokenService.save(
			RefreshToken.of(user.getId(), refreshToken, toSeconds(refreshTokenProvider.getExpiryDate(refreshToken))));
		return JwtPair.of(accessToken, refreshToken);
	}

	public Pair<Long, JwtPair> refresh(String refreshToken) {
		Map<String, ?> claims = refreshTokenProvider.getJwtClaimsFromToken(refreshToken).getClaims();

		Long userId = Long.parseLong((String)claims.get(RefreshTokenClaimKeys.USER_ID.getValue()));
		String role = (String)claims.get(RefreshTokenClaimKeys.ROLE.getValue());

		String newAccessToken = accessTokenProvider.generateToken(AccessTokenClaim.of(userId, role));
		RefreshToken newRefreshToken;

		try {
			newRefreshToken = refreshTokenService.refresh(userId, refreshToken,
				refreshTokenProvider.generateToken(RefreshTokenClaim.of(userId, role)));
		} catch (IllegalArgumentException e) {
			throw new CommonException(ExceptionCode.EXPIRED_TOKEN);
		} catch (IllegalStateException e) {
			throw new CommonException(ExceptionCode.TAKEN_AWAY_TOKEN);
		}

		return Pair.of(userId, JwtPair.of(newAccessToken, newRefreshToken.getToken()));
	}

	public void removeAccessTokenAndRefreshToken(Long userId, String accessToken, String refreshToken) {
		JwtClaims jwtClaims = null;
		if (refreshToken != null) {
			try {
				jwtClaims = refreshTokenProvider.getJwtClaimsFromToken(refreshToken);
			} catch (JwtException ex) {
				if (!(ex instanceof ExpiredJwtException)) {
					throw ex;
				}
			}
		}

		if (jwtClaims != null) {
			deleteRefreshToken(userId, jwtClaims, refreshToken);
		}

		deleteAccessToken(userId, accessToken);
	}

	private void deleteRefreshToken(Long userId, JwtClaims jwtClaims, String refreshToken) {
		Long refreshTokenUserId = Long.parseLong(
			(String)jwtClaims.getClaims().get(RefreshTokenClaimKeys.USER_ID.getValue()));

		if (!userId.equals(refreshTokenUserId)) {
			log.warn("소유권이 없는 RT에 대한 삭제 요청 . userId : {}", userId);
			throw new CommonException(ExceptionCode.TAKEN_AWAY_TOKEN);
		}

		try {
			refreshTokenService.delete(refreshTokenUserId, refreshToken);
		} catch (IllegalArgumentException e) {
			log.warn("refresh token not found. userId : {}", userId);
		}
	}

	private void deleteAccessToken(Long userId, String accessToken) {
		LocalDateTime expiresAt = accessTokenProvider.getExpiryDate(accessToken);
		forbiddenTokenService.createForbiddenToken(accessToken, userId, expiresAt);
	}

	private long toSeconds(LocalDateTime expiryTime) {
		return Duration.between(LocalDateTime.now(), expiryTime).getSeconds();
	}
}
