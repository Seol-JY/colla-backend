package one.colla.global.config.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
			"https://api.colla.so",
			"https://colla.so",
			"http://localhost:3000"

		));
		configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "PUT", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of(
			HttpHeaders.AUTHORIZATION,
			HttpHeaders.SET_COOKIE,
			HttpHeaders.CONTENT_TYPE,
			HttpHeaders.ACCEPT,
			HttpHeaders.UPGRADE,
			HttpHeaders.CONNECTION,
			HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
			HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
			HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
			HttpHeaders.ACCESS_CONTROL_MAX_AGE,
			HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
			HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
			HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,

			"Sec-WebSocket-Accept",
			"Sec-WebSocket-Protocol")
		);
		configuration.setMaxAge(3600L);
		configuration.setAllowCredentials(true);
		configuration.addExposedHeader("Access-Control-Allow-Origin");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
