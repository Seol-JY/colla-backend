package one.colla.auth.application.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import one.colla.auth.config.GoogleOAuthProperties;
import one.colla.auth.config.KakaoOAuthProperties;
import one.colla.auth.config.NaverOAuthProperties;
import one.colla.auth.config.OAuthProperties;
import one.colla.auth.config.OAuthPropertyFactory;
import one.colla.common.CommonTest;
import one.colla.user.domain.Provider;

class OAuthPropertiesFactoryTest extends CommonTest {

	@Autowired
	private OAuthPropertyFactory oAuthPropertyFactory;

	private static Stream<Arguments> provideOAuthPropertiesFromProvider() {

		return Stream.of(
			Arguments.of(Provider.GOOGLE, mock(GoogleOAuthProperties.class)),
			Arguments.of(Provider.KAKAO, mock(KakaoOAuthProperties.class)),
			Arguments.of(Provider.NAVER, mock(NaverOAuthProperties.class))
		);
	}

	@MethodSource("provideOAuthPropertiesFromProvider")
	@DisplayName("Provider에 해당하는 OAuthProperties를 응답 받을 수 있다.")
	@ParameterizedTest
	void createOAuthProperties(Provider provider, OAuthProperties oAuthProperties) {

		// when
		OAuthProperties authProperties = oAuthPropertyFactory.createOAuthProperty(provider);

		// then
		assertThat(authProperties).isInstanceOf(oAuthProperties.getClass());

	}

}
