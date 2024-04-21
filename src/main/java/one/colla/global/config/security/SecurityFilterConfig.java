package one.colla.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import one.colla.common.security.filter.JwtAuthenticationFilter;
import one.colla.common.security.filter.JwtExceptionFilter;
import one.colla.common.security.jwt.JwtProvider;
import one.colla.infra.redis.forbidden.ForbiddenTokenService;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityFilterConfig {
	private final ObjectMapper objectMapper;

	private final UserDetailsService userDetailServiceImpl;
	private final JwtProvider accessTokenProvider;
	private final ForbiddenTokenService forbiddenTokenService;

	@Bean
	public JwtExceptionFilter jwtExceptionFilter() {
		return new JwtExceptionFilter(objectMapper);
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(userDetailServiceImpl, accessTokenProvider, forbiddenTokenService);
	}
}
