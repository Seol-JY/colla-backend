package one.colla.common.util.oauth;

import one.colla.auth.application.dto.response.OauthLoginUrlResponse;
import one.colla.auth.config.OAuthProperties;

public interface OAuthUriGenerator {

	OauthLoginUrlResponse generate(OAuthProperties oAuthProperties);
}
