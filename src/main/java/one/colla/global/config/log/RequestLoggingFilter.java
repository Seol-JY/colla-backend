package one.colla.global.config.log;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import one.colla.common.security.authentication.CustomUserDetails;

@Component
@Order(10)
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final String USER_ID = "userId";
	private static final String REQUEST_ID = "requestId";
	private static final String IP = "clientIp";
	private static final String USER_AGENT = "userAgent";
	private static final String REQUEST_URI = "uri";
	private static final String HTTP_METHOD = "httpMethod";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		try {
			MDC.put(REQUEST_ID, UUID.randomUUID().toString());

			String clientIp = getClientIp(request);
			MDC.put(IP, clientIp);

			String userAgent = request.getHeader("User-Agent");
			MDC.put(USER_AGENT, userAgent != null ? userAgent : "unknown");

			MDC.put(REQUEST_URI, request.getRequestURI());
			MDC.put(HTTP_METHOD, request.getMethod());

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails userDetails) {
				MDC.put(USER_ID, ((CustomUserDetails)userDetails).getUserId().toString());
			}

			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
