package one.colla.common.security.jwt.access;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.ExpiredJwtException;
import one.colla.common.security.jwt.JwtClaims;
import one.colla.common.security.jwt.JwtProvider;

@ExtendWith(MockitoExtension.class)
class AccessTokenProviderTest {
	final String secretString = "secretStringForTestingThisByteArrayIs128bits";

	private JwtProvider jwtProvider;
	private JwtClaims jwtClaims;

	@BeforeEach
	void setUp() {
		jwtProvider = new AccessTokenProvider(secretString, Duration.ofMinutes(5));
		jwtClaims = AccessTokenClaim.of(1L, "ROLE_USER");
	}

	@Test
	@DisplayName("토큰 생성이 정상적으로 이루어진다.")
	void tokenCreationIsSuccessful() {
		// when
		String token = jwtProvider.generateToken(jwtClaims);

		// then
		assertThat(token).isNotNull();
	}

	@Test
	@DisplayName("토큰에서 추출한 정보가 실제와 일치하다.")
	void extractedTokenInformationMatchesReality() {
		// given
		String token = jwtProvider.generateToken(jwtClaims);

		// when
		JwtClaims actualJwtClaims = jwtProvider.getJwtClaimsFromToken(token);

		// then
		assertThat(actualJwtClaims.getClaims()).isEqualTo(jwtClaims.getClaims());
	}

	@Test
	@DisplayName("토큰이 만료되면 ExpiredJwtException 예외가 발생한다.")
	void expiredTokenRaisesExpiredJwtException() {
		// given
		JwtProvider zeroExpirationAccessTokenProvider = new AccessTokenProvider(secretString, Duration.ofMillis(0));
		String token = zeroExpirationAccessTokenProvider.generateToken(jwtClaims);

		// when then
		assertThatThrownBy(() -> zeroExpirationAccessTokenProvider.getJwtClaimsFromToken(token)).isExactlyInstanceOf(
			ExpiredJwtException.class);
	}
}
