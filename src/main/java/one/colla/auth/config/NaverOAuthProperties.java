package one.colla.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class NaverOAuthProperties implements OAuthProperties {

	@Value("${spring.oauth.naver.client-id}")
	private String clientId;

	@Value("${spring.oauth.naver.client-secret}")
	private String clientSecret;

	@Value("${spring.oauth.naver.end-point}")
	private String endPoint;

	@Value("${spring.oauth.naver.response-type}")
	private String responseType;

	@Value("${spring.oauth.naver.scopes}")
	private List<String> scopes;

	@Value("${spring.oauth.naver.access-type}")
	private String accessType;

	@Value("${spring.oauth.naver.token-uri}")
	private String tokenUri;

	@Value("${spring.oauth.naver.redirect-uri}")
	private String redirectUri;
}
