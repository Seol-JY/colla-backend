package one.colla.common.security.authentication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAnonymousSecurityContextFactory.class)
public @interface WithMockAnonymous {
}
