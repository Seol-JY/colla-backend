package one.colla.common.security.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
	private static final String CHARACTER_ENCODING = "UTF-8";

	private static final Map<Class<? extends JwtException>, ExceptionCode> JWT_EXCEPTION_CODE_MAP = Map.of(
		ExpiredJwtException.class, ExceptionCode.EXPIRED_TOKEN,
		MalformedJwtException.class, ExceptionCode.MALFORMED_TOKEN,
		SignatureException.class, ExceptionCode.TAMPERED_TOKEN,
		UnsupportedJwtException.class, ExceptionCode.UNSUPPORTED_JWT_TOKEN
	);

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException, IOException {
		try {
			filterChain.doFilter(request, response);

		} catch (JwtException ex) {
			Class<? extends Exception> exceptionClass = ex.getClass();
			ExceptionCode exceptionCode = JWT_EXCEPTION_CODE_MAP.get(exceptionClass);
			sendAuthError(response, exceptionCode);
		} catch (CommonException ex) {
			if (ex.getErrorCode() == 40182) {
				sendAuthError(response, ex);
			}
		}
	}

	private void sendAuthError(HttpServletResponse response, ExceptionCode ec) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(CHARACTER_ENCODING);
		response.setStatus(ec.getHttpStatus().value());
		objectMapper.writeValue(response.getWriter(), ApiResponse.createErrorResponse(ec));
	}

	private void sendAuthError(HttpServletResponse response, CommonException ex) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(CHARACTER_ENCODING);
		response.setStatus(ex.getHttpStatus().value());
		objectMapper.writeValue(response.getWriter(), ApiResponse.createErrorResponse(ex));
	}
}
