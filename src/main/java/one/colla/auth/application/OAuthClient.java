package one.colla.auth.application;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import one.colla.auth.application.dto.oauth.OAuthTokenResponse;
import one.colla.auth.config.OAuthProperties;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@Slf4j
@Component
public class OAuthClient {

	private static final String OAUTH_GRANT_TYPE = "authorization_code";
	private final RestTemplate restTemplate;

	public OAuthClient(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	public OAuthTokenResponse getAccessToken(final OAuthProperties oAuthProperties, final String code) {
		try {

			String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);
			ResponseEntity<OAuthTokenResponse> response = restTemplate.postForEntity(
				oAuthProperties.getTokenUri(),
				createRequestEntity(oAuthProperties, decode),
				OAuthTokenResponse.class
			);
			return response.getBody();
		} catch (HttpClientErrorException ex) {
			log.error("Authorization code가 올바르지 않습니다.", ex);
			throw new CommonException(ExceptionCode.INVALID_AUTHORIZATION_CODE);
		}
	}

	private HttpEntity<MultiValueMap<String, String>> createRequestEntity(
		final OAuthProperties oAuthProperties,
		final String code) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return new HttpEntity<>(generateParams(oAuthProperties, code), headers);
	}

	private MultiValueMap<String, String> generateParams(final OAuthProperties oAuthProperties, final String code) {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", oAuthProperties.getClientId());
		params.add("client_secret", oAuthProperties.getClientSecret());
		params.add("redirect_uri", oAuthProperties.getRedirectUri());
		params.add("grant_type", OAUTH_GRANT_TYPE);
		return params;
	}

}
