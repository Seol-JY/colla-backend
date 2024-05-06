package one.colla.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class GoogleOAuthProperties implements OAuthProperties {

	@Value("${spring.oauth.google.client-id}")
	private String clientId;

	@Value("${spring.oauth.google.client-secret}")
	private String clientSecret;

	@Value("${spring.oauth.google.end-point}")
	private String endPoint;

	@Value("${spring.oauth.google.response-type}")
	private String responseType;

	@Value("${spring.oauth.google.scopes}")
	private List<String> scopes;

	@Value("${spring.oauth.google.access-type}")
	private String accessType;

	@Value("${spring.oauth.google.token-uri}")
	private String tokenUri;

	@Value("${spring.oauth.google.redirect-uri}")
	private String redirectUri;
}
