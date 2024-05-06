package one.colla.common.util.oauth;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import one.colla.auth.application.dto.response.OauthLoginUrlResponse;
import one.colla.auth.config.OAuthProperties;

@RequiredArgsConstructor
@Component
public class OAuthUriGeneratorImpl implements OAuthUriGenerator {
	private static final String QUERY_START_MARK = "?";
	private static final String QUERY_AND_MARK = "&";
	private static final String QUERY_PARAM_CLIENT_ID = "client_id=";
	private static final String QUERY_PARAM_REDIRECT_URI = "redirect_uri=";
	private static final String QUERY_PARAM_RESPONSE_TYPE = "response_type=";
	private static final String QUERY_PARAM_SCOPE = "scope=";
	private static final String QUERY_PARAM_SCOPE_DELIMITER = "+";
	private static final String QUERY_PARAM_ACCESS_TYPE = "access_type=";

	@Override
	public OauthLoginUrlResponse generate(OAuthProperties oAuthProperties) {
		StringBuilder sb = new StringBuilder();
		StringBuilder url = sb.append(oAuthProperties.getEndPoint())
			.append(QUERY_START_MARK)
			.append(QUERY_PARAM_CLIENT_ID).append(oAuthProperties.getClientId())
			.append(QUERY_AND_MARK)
			.append(QUERY_PARAM_REDIRECT_URI).append(oAuthProperties.getRedirectUri())
			.append(QUERY_AND_MARK)
			.append(QUERY_PARAM_RESPONSE_TYPE).append(oAuthProperties.getResponseType())
			.append(QUERY_AND_MARK)
			.append(QUERY_PARAM_SCOPE).append(String.join(QUERY_PARAM_SCOPE_DELIMITER, oAuthProperties.getScopes()))
			.append(QUERY_AND_MARK)
			.append(QUERY_PARAM_ACCESS_TYPE).append(oAuthProperties.getAccessType());
		return new OauthLoginUrlResponse(url.toString());
	}

}
