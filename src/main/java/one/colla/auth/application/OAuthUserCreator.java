package one.colla.auth.application;

import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import one.colla.auth.application.dto.oauth.OAuthTokenResponse;
import one.colla.auth.application.dto.oauth.OAuthUserInfo;
import one.colla.common.util.TokenParser;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.user.domain.Provider;
import one.colla.user.domain.vo.Username;

@Component
public class OAuthUserCreator {

	private static final String NAVER_PROFILE_URL = "https://openapi.naver.com/v1/nid/me";
	private static final int USERNAME_MAX_LENGTH = Username.MAX_LENGTH;

	private final RestTemplate restTemplate;
	private final TokenParser tokenParser;

	public OAuthUserCreator(RestTemplateBuilder restTemplateBuilder, TokenParser tokenParser) {
		this.restTemplate = restTemplateBuilder.build();
		this.tokenParser = tokenParser;
	}

	public OAuthUserInfo createUser(OAuthTokenResponse tokenResponse, Provider provider) {
		return switch (provider) {
			case NAVER -> createNaverUser(tokenResponse.accessToken());
			case GOOGLE, KAKAO -> createOtherUser(tokenResponse.idToken(), provider);
		};
	}

	private OAuthUserInfo createNaverUser(String accessToken) {
		if (accessToken == null) {
			throw new CommonException(ExceptionCode.INVALID_AUTHORIZATION_CODE);
		}
		JsonNode userResourceNode = getNaverUserResource(accessToken);
		JsonNode userInfo = userResourceNode.get("response");
		String email = userInfo.get("email").asText();
		String username = userInfo.get("nickname").asText();
		String picture = userInfo.get("profile_image").asText();
		return new OAuthUserInfo(email, adjustNameLength(username), picture);
	}

	private OAuthUserInfo createOtherUser(String idToken, Provider provider) {
		Map<String, Object> payload = tokenParser.parseIdTokenPayload(idToken);
		String email = (String)payload.get("email");
		String username = extractNameFromPayload(payload, provider);
		String picture = (String)payload.get("picture");
		return new OAuthUserInfo(email, adjustNameLength(username), picture);
	}

	private String extractNameFromPayload(Map<String, Object> payload, Provider provider) {
		return switch (provider) {
			case GOOGLE -> (String)payload.get("name");
			case KAKAO -> (String)payload.get("nickname");
			default -> throw new CommonException(ExceptionCode.INVALID_OAUTH_PROVIDER);
		};
	}

	private String adjustNameLength(String name) {
		return name != null && name.length() > USERNAME_MAX_LENGTH ? name.substring(0, USERNAME_MAX_LENGTH) : name;
	}

	private JsonNode getNaverUserResource(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<?> request = new HttpEntity<>(headers);
		return restTemplate.exchange(NAVER_PROFILE_URL, HttpMethod.GET, request, JsonNode.class)
			.getBody();
	}
}
