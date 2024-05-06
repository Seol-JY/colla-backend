package one.colla.common.util.oauth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import one.colla.auth.application.dto.response.OauthLoginUrlResponse;
import one.colla.auth.config.OAuthProperties;
import one.colla.common.CommonTest;

class OAuthUriGeneratorImplTest extends CommonTest {

	@Autowired
	private OAuthUriGeneratorImpl OAuthUriGeneratorImpl1;

	final String END_POINT = "endPoint";
	final String CLIENT_ID = "clientId";
	final String REDIRECT_URI = "redirectUri";
	final String RESPONSE_TYPE = "responseType";
	final List<String> SCOPES = List.of("scopes1", "scopes2");
	final String ACCESS_TYPE = "accessType";

	@Test
	@DisplayName("Provider에 따라 올바른 OAuth URL을 생성할 수 있다.")
	void testGenerate() {

		// given
		OAuthProperties oAuthProperties = mock(OAuthProperties.class);
		given(oAuthProperties.getEndPoint()).willReturn(END_POINT);
		given(oAuthProperties.getClientId()).willReturn(CLIENT_ID);
		given(oAuthProperties.getRedirectUri()).willReturn(REDIRECT_URI);
		given(oAuthProperties.getResponseType()).willReturn(RESPONSE_TYPE);
		given(oAuthProperties.getScopes()).willReturn(SCOPES);
		given(oAuthProperties.getAccessType()).willReturn(ACCESS_TYPE);

		OauthLoginUrlResponse expectedUrl = new OauthLoginUrlResponse(END_POINT +
			"?client_id=" + CLIENT_ID +
			"&redirect_uri=" + REDIRECT_URI +
			"&response_type=" + RESPONSE_TYPE +
			"&scope=" + String.join("+", SCOPES) +
			"&access_type=" + ACCESS_TYPE);

		// when
		OauthLoginUrlResponse actualUrl = OAuthUriGeneratorImpl1.generate(oAuthProperties);

		// then
		assertThat(expectedUrl).isEqualTo(actualUrl);
	}
}
