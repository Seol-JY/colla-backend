package one.colla.global.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.presentation.ApiResponse;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	private static final Map<Class<? extends JwtException>, ExceptionCode> JWT_EXCEPTION_CODE_MAP = Map.of(
		ExpiredJwtException.class, ExceptionCode.EXPIRED_TOKEN,
		MalformedJwtException.class, ExceptionCode.MALFORMED_TOKEN,
		SignatureException.class, ExceptionCode.TAMPERED_TOKEN,
		UnsupportedJwtException.class, ExceptionCode.UNSUPPORTED_JWT_TOKEN
	);

	@ExceptionHandler(CommonException.class)
	public ResponseEntity<ApiResponse<CommonException>> handleCommonException(CommonException ex) {
		return ApiResponse.createErrorResponseEntity(ex);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
		MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ApiResponse.createValidationResponseEntity(errors);
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiResponse<CommonException>> handleJwtException(JwtException ex) {
		Class<? extends Exception> exceptionClass = ex.getClass();
		ExceptionCode exceptionCode = JWT_EXCEPTION_CODE_MAP.get(exceptionClass);
		return ApiResponse.createErrorResponseEntity(exceptionCode);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleUnexpectedExceptions(Exception ex) {
		return ApiResponse.createServerErrorResponseEntity();
	}
}
