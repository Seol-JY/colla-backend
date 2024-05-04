package one.colla.common.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import one.colla.common.fixtures.UserFixtures;

public class WithMockCustomUserSecurityContextFactory
	implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		CustomUserDetails userDetails = UserFixtures.createCustomUserDetailsByUserId(
			Long.parseLong(annotation.userId()));
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());
		context.setAuthentication(authentication);
		return context;
	}
}
