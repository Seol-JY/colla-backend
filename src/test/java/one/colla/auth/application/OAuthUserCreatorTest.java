package one.colla.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import one.colla.auth.application.dto.oauth.OAuthTokenResponse;
import one.colla.auth.application.dto.oauth.OAuthUserInfo;
import one.colla.common.CommonTest;
import one.colla.common.util.TokenParser;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.user.domain.OauthProvider;

public class OAuthUserCreatorTest extends CommonTest {

	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final String GOOGLE_ID_TOKEN = "google_id_token_dummy";
	private static final String KAKAO_ID_TOKEN = "kakao_id_token_dummy";
	private static final String GOOGLE_EMAIL = "googleuser@example.com";
	private static final String GOOGLE_NICKNAME = "GoogleUser";
	private static final String GOOGLE_PICTURE = "http://example.com/googleuser.jpg";
	private static final String KAKAO_EMAIL = "kakaouser@example.com";
	private static final String KAKAO_NICKNAME = "KakaoUser";
	private static final String KAKAO_PICTURE = "http://example.com/kakaouser.jpg";
	private static final String EMAIL = "email@example.com";
	private static final String NICKNAME = "nickname";
	private static final String PICTURE_URL = "http://prcture.com";

	@Mock
	private TokenParser tokenParser;

	@Mock
	private RestTemplateBuilder restTemplateBuilder;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private OAuthUserCreator oAuthUserCreator;

	@BeforeEach
	public void setUp() {
		given(restTemplateBuilder.build()).willReturn(restTemplate);
		oAuthUserCreator = new OAuthUserCreator(restTemplateBuilder, tokenParser);
	}

	@Test
	@DisplayName("OAuth 사용자 생성 할 수 있다 - 네이버")
	public void testCreateUserForNaver() {
		OAuthTokenResponse tokenResponse = new OAuthTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, null);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode userInfo = mapper.createObjectNode();
		userInfo.put("email", EMAIL);
		userInfo.put("nickname", NICKNAME);
		userInfo.put("profile_image", PICTURE_URL);

		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.set("response", userInfo);

		given(restTemplate.exchange(
			eq("https://openapi.naver.com/v1/nid/me"),
			eq(HttpMethod.GET),
			any(HttpEntity.class),
			eq(JsonNode.class)))
			.willReturn(ResponseEntity.ok(responseNode));

		OAuthUserInfo oAuthUserInfo = oAuthUserCreator.createUser(tokenResponse, OauthProvider.NAVER);

		assertSoftly(softly -> {
			softly.assertThat(oAuthUserInfo.email()).isEqualTo(EMAIL);
			softly.assertThat(oAuthUserInfo.nickname()).isEqualTo(NICKNAME);
			softly.assertThat(oAuthUserInfo.picture()).isEqualTo(PICTURE_URL);
		});
	}

	@Test
	@DisplayName("네이버 사용자 생성 실패 - 유효하지 않은 액세스 토큰")
	public void testCreateNaverUserWithInvalidAccessToken() {

		//given
		OAuthTokenResponse tokenResponse = new OAuthTokenResponse(null, null, null);

		// when & then
		assertThatThrownBy(() -> oAuthUserCreator.createUser(tokenResponse, OauthProvider.NAVER))
			.isInstanceOf(CommonException.class)
			.hasMessageContaining(ExceptionCode.INVALID_AUTHORIZATION_CODE.getMessage());
	}

	@Test
	@DisplayName("OAuth 사용자 생성 할 수 있다. - 구글 및 카카오")
	public void testCreateUserForGoogleAndKakao() {

		// given
		Map<String, Object> googlePayload = new HashMap<>();
		googlePayload.put("email", GOOGLE_EMAIL);
		googlePayload.put("name", GOOGLE_NICKNAME);
		googlePayload.put("picture", GOOGLE_PICTURE);

		Map<String, Object> kakaoPayload = new HashMap<>();
		kakaoPayload.put("email", KAKAO_EMAIL);
		kakaoPayload.put("nickname", KAKAO_NICKNAME);
		kakaoPayload.put("picture", KAKAO_PICTURE);

		given(tokenParser.parseIdTokenPayload(GOOGLE_ID_TOKEN)).willReturn(googlePayload);
		given(tokenParser.parseIdTokenPayload(KAKAO_ID_TOKEN)).willReturn(kakaoPayload);

		OAuthTokenResponse googleTokenResponse = new OAuthTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, GOOGLE_ID_TOKEN);
		OAuthTokenResponse kakaoTokenResponse = new OAuthTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN, KAKAO_ID_TOKEN);

		// when
		OAuthUserInfo googleUser = oAuthUserCreator.createUser(googleTokenResponse, OauthProvider.GOOGLE);
		OAuthUserInfo kakaoUser = oAuthUserCreator.createUser(kakaoTokenResponse, OauthProvider.KAKAO);

		// then
		assertSoftly(softly -> {
			softly.assertThat(googleUser.email()).isEqualTo(GOOGLE_EMAIL);
			softly.assertThat(googleUser.nickname()).isEqualTo(GOOGLE_NICKNAME);
			softly.assertThat(googleUser.picture()).isEqualTo(GOOGLE_PICTURE);

			softly.assertThat(kakaoUser.email()).isEqualTo(KAKAO_EMAIL);
			softly.assertThat(kakaoUser.nickname()).isEqualTo(KAKAO_NICKNAME);
			softly.assertThat(kakaoUser.picture()).isEqualTo(KAKAO_PICTURE);
		});
	}

}
