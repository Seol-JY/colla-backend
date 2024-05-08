package one.colla.auth.config;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.user.domain.OauthProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthPropertyFactory {

	private final GoogleOAuthProperties googleOAuthProperties;
	private final KakaoOAuthProperties kakaoOAuthProperties;
	private final NaverOAuthProperties naverOAuthProperties;

	public OAuthProperties createOAuthProperty(final OauthProvider oauthProvider) {
		return switch (oauthProvider) {
			case GOOGLE -> googleOAuthProperties;
			case KAKAO -> kakaoOAuthProperties;
			case NAVER -> naverOAuthProperties;
		};
	}

}
