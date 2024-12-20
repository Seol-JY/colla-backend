package one.colla.global.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import one.colla.common.security.filter.JwtAuthenticationFilter;
import one.colla.common.security.filter.JwtExceptionFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private static final String[] SWAGGER_ENDPOINTS = {"/api-docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger",
		"/docs/open-api-3.0.1.yaml"};
	private static final String[] PUBLIC_ENDPOINTS = {"/", "/error"};
	private static final String[] ANONYMOUS_ENDPOINTS = {"/api/v1/auth/**"};
	private static final String[] CONDITIONAL_AUTHENTICATION_GET_ENDPOINTS = {"/api/v1/teamspaces"};

	@Value("${management.endpoints.web.base-path}")
	private String actuatorPath;

	private final CorsConfigurationSource corsConfigurationSource;
	private final AccessDeniedHandler accessDeniedHandler;
	private final AuthenticationEntryPoint authenticationEntryPoint;

	private final DaoAuthenticationProvider daoAuthenticationProvider;
	private final JwtExceptionFilter jwtExceptionFilter;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return defaultSecurity(http)
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.authorizeHttpRequests(
				request -> defaultAuthorizeHttpRequests(request)
					.requestMatchers(SWAGGER_ENDPOINTS).permitAll()
					.requestMatchers(actuatorPath + "/**").permitAll()
					.anyRequest().authenticated()
			).build();
	}

	private HttpSecurity defaultSecurity(HttpSecurity http) throws Exception {
		return http.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)

			.authenticationProvider(daoAuthenticationProvider)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)

			.exceptionHandling(exception -> exception
				.accessDeniedHandler(accessDeniedHandler)
				.authenticationEntryPoint(authenticationEntryPoint));
	}

	private AbstractRequestMatcherRegistry
		<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl> defaultAuthorizeHttpRequests(
		AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
		return auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
			.requestMatchers(HttpMethod.OPTIONS, "*").permitAll()
			.requestMatchers("/ws-stomp/**").permitAll()
			.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
			.requestMatchers(HttpMethod.GET, CONDITIONAL_AUTHENTICATION_GET_ENDPOINTS).permitAll()
			.requestMatchers(ANONYMOUS_ENDPOINTS).anonymous();
	}
}
