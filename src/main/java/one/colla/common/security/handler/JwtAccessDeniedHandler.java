package one.colla.common.security.handler;

import static one.colla.global.exception.ExceptionCode.*;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;

@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	private static final String CHARACTER_ENCODING = "UTF-8";
	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(CHARACTER_ENCODING);
		response.setStatus(FORBIDDEN_ACCESS_TOKEN.getHttpStatus().value());
		objectMapper.writeValue(response.getWriter(), ApiResponse.createErrorResponse(FORBIDDEN_ACCESS_TOKEN));
	}

}
