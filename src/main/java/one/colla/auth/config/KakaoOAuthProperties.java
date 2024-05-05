package one.colla.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class KakaoOAuthProperties implements OAuthProperties {

	@Value("${spring.oauth.kakao.client-id}")
	private String clientId;

	@Value("${spring.oauth.kakao.client-secret}")
	private String clientSecret;

	@Value("${spring.oauth.kakao.end-point}")
	private String endPoint;

	@Value("${spring.oauth.kakao.response-type}")
	private String responseType;

	@Value("${spring.oauth.kakao.scopes}")
	private List<String> scopes;

	@Value("${spring.oauth.kakao.access-type}")
	private String accessType;

	@Value("${spring.oauth.kakao.token-uri}")
	private String tokenUri;

	@Value("${spring.oauth.kakao.redirect-uri}")
	private String redirectUri;
}
