package one.colla.auth.config;

import java.util.List;

public interface OAuthProperties {

	String getClientId();

	String getClientSecret();

	String getEndPoint();

	String getResponseType();

	List<String> getScopes();

	String getAccessType();

	String getTokenUri();

	String getRedirectUri();
}
