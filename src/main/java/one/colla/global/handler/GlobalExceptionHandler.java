package one.colla.global.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.presentation.ApiResponse;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.global.exception.VoException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
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

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<Object> handleJwtException(JwtException ex) {
		Class<? extends Exception> exceptionClass = ex.getClass();
		ExceptionCode exceptionCode = JWT_EXCEPTION_CODE_MAP.get(exceptionClass);
		return ApiResponse.createErrorResponseEntity(exceptionCode);
	}

	@ExceptionHandler(VoException.class)
	public ResponseEntity<ApiResponse<String>> handleVoErrorExceptions(VoException ex) {
		return ApiResponse.createErrorResponseEntity(ex);
	}

	@Override
	@Nullable
	protected ResponseEntity<Object> handleNoResourceFoundException(
		NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		return ApiResponse.createErrorResponseEntity(ExceptionCode.NOT_FOUND_RESOURCE);
	}

	@Override
	@Nullable
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ApiResponse.createValidationResponseEntity(errors);
	}

	@Override
	@Nullable
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
		MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String missingParam = ex.getParameterName();
		Map<String, String> errors = new HashMap<>();
		errors.put(missingParam, String.format("필수 쿼리 파라미터 '%s'가 누락되었습니다.", missingParam));
		return ApiResponse.createValidationResponseEntity(errors);
	}

	@Override
	@Nullable
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
		HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return ApiResponse.createErrorResponseEntity(ExceptionCode.METHOD_FORBIDDEN);
	}

	@Override
	@Nullable
	protected ResponseEntity<Object> handleExceptionInternal(
		Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

		if (request instanceof ServletWebRequest servletWebRequest) {
			HttpServletResponse response = servletWebRequest.getResponse();
			if (response != null && response.isCommitted()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Response already committed. Ignoring: " + ex);
				}
				return null;
			}
		}
		logger.error("Unexpected error 발생: " + ex.getMessage(), ex);
		return ApiResponse.createServerErrorResponseEntity();
	}

	@Override
	@Nullable
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
		HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		if (ex.getCause() instanceof MismatchedInputException mismatchedInputException) {
			for (JsonMappingException.Reference reference : mismatchedInputException.getPath()) {
				errors.put(reference.getFieldName(), "필드의 값이 잘못되었습니다. Type 을 확인하세요.");
			}
		} else {
			errors.put("common", "확인할 수 없는 형태의 데이터가 들어왔습니다. JSON 형식인지 확인하세요.");
			log.error("HttpMessageNotReadable 에러 발생: {}", ex.getMessage(), ex);
		}
		return ApiResponse.createValidationResponseEntity(errors);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<String>> handleConflict(MethodArgumentTypeMismatchException ex) {
		return ApiResponse.createErrorResponseEntity();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleCommonException(Exception ex) {
		logger.error("Unexpected error 발생: " + ex.getMessage(), ex);
		return ApiResponse.createServerErrorResponseEntity();
	}
}
