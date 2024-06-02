package one.colla.common.security.interceptor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.jwt.JwtClaims;
import one.colla.common.security.jwt.access.AccessTokenClaimKeys;
import one.colla.common.security.jwt.access.AccessTokenProvider;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

@RequiredArgsConstructor
@Component
@Slf4j
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

	private final AccessTokenProvider accessTokenProvider;
	private final UserRepository userRepository;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		if (request instanceof ServletServerHttpRequest) {
			return handleHandshake((ServletServerHttpRequest)request, response, attributes);
		}

		log.warn("요청이 ServletServerHttpRequest 타입이 아님");
		response.setStatusCode(HttpStatus.BAD_REQUEST);
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception ex) {
		// 핸드셰이크 후 처리 로직 (필요 시)
	}

	private boolean handleHandshake(ServletServerHttpRequest servletRequest, ServerHttpResponse response,
		Map<String, Object> attributes) {
		HttpServletRequest request = servletRequest.getServletRequest();
		String accessToken = request.getParameter("accessToken");

		if (accessToken == null) {
			log.warn("액세스 토큰이 비어있음");
			response.setStatusCode(HttpStatus.BAD_REQUEST);
			return false;
		}

		return validateAccessToken(accessToken, response, attributes);
	}

	private boolean validateAccessToken(String accessToken, ServerHttpResponse response,
		Map<String, Object> attributes) {
		try {
			JwtClaims claims = accessTokenProvider.getJwtClaimsFromToken(accessToken);
			String userId = (String)claims.getClaims().get(AccessTokenClaimKeys.USER_ID.getValue());
			User user = userRepository.findById(Long.valueOf(userId))
				.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

			attributes.put("userId", user.getId());
			return true;
		} catch (JwtException e) {
			log.error("유효하지 않은 접근 토큰: {}", e.getMessage());
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		} catch (Exception e) {
			log.error("WebSocket 핸드셰이크 중 오류 발생: {}", e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return false;
		}
	}
}
