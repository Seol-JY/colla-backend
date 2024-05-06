package one.colla.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import one.colla.auth.application.dto.oauth.OAuthTokenResponse;
import one.colla.auth.config.OAuthProperties;
import one.colla.common.CommonTest;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

public class OAuthClientTest extends CommonTest {

	@Mock
	private RestTemplateBuilder restTemplateBuilder;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	static OAuthProperties oAuthProperties;

	@InjectMocks
	private OAuthClient oAuthClient;

	private static final String TOKEN_URI = "http://www.example.com";
	private static final String AUTHORIZATION_CODE = "authorizationCode";

	@BeforeEach
	public void setUp() {
		when(restTemplateBuilder.build()).thenReturn(restTemplate);
		oAuthClient = new OAuthClient(restTemplateBuilder);
		given(oAuthProperties.getTokenUri()).willReturn(TOKEN_URI);
	}

	@Test
	@DisplayName("올바른 Aithorization code로 accessToken, refreshToken, idToken을 발급 받을 수 있다.")
	public void testGetAccessTokenSuccess() throws Exception {

		// given
		String ACCESS_TOKEN = "accessToken";
		String REFRESH_TOKEN = "refreshToken";
		String ID_TOKEN = "idToken";

		OAuthTokenResponse mockResponse = mock(OAuthTokenResponse.class);

		given(mockResponse.accessToken()).willReturn(ACCESS_TOKEN);
		given(mockResponse.refreshToken()).willReturn(REFRESH_TOKEN);
		given(mockResponse.idToken()).willReturn(ID_TOKEN);

		given(restTemplate.postForEntity(eq(oAuthProperties.getTokenUri()), any(), eq(OAuthTokenResponse.class)))
			.willReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

		// when
		OAuthTokenResponse result = oAuthClient.getAccessToken(oAuthProperties, AUTHORIZATION_CODE);

		// then
		assertThat(result).isNotNull();
		assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
		assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
		assertThat(result.idToken()).isEqualTo(ID_TOKEN);

	}

	@Test
	@DisplayName("잘못된 Aithorization code로 token을 발급시 예외를 발생한다.")
	public void testGetAccessTokenFail() {

		// given
		given(restTemplate.postForEntity(
			eq(oAuthProperties.getTokenUri()),
			any(),
			eq(OAuthTokenResponse.class)))
			.willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

		// when/then
		assertThatThrownBy(() -> oAuthClient.getAccessToken(oAuthProperties, AUTHORIZATION_CODE))
			.isInstanceOf(CommonException.class)
			.hasMessageContaining(ExceptionCode.INVALID_AUTHORIZATION_CODE.getMessage());
	}

}
