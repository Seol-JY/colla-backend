package one.colla.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import one.colla.common.presentation.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CommonException.class)
	public ResponseEntity<ApiResponse<?>> handleGlobalException(CommonException ex) {
		return ResponseEntity
			.status(ex.getHttpStatus())
			.body(ApiResponse.createErrorResponse(ex));
	}
}
