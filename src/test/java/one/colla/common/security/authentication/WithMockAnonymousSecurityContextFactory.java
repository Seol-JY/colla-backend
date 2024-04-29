package one.colla.common.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockAnonymousSecurityContextFactory
	implements WithSecurityContextFactory<WithMockAnonymous> {

	@Override
	public SecurityContext createSecurityContext(WithMockAnonymous annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			null, null, null);
		context.setAuthentication(authentication);
		return context;
	}
}
