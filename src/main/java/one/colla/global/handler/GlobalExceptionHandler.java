package one.colla.global.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import one.colla.common.presentation.ApiResponse;
import one.colla.global.exception.CommonException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CommonException.class)
	public ResponseEntity<ApiResponse<?>> handleGlobalException(CommonException ex) {
		return ApiResponse.createErrorResponseEntity(ex);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
		MethodArgumentNotValidException exception) {
		Map<String, String> errors = new HashMap<>();
		exception.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ApiResponse.createValidationResponseEntity(errors);
	}

}
