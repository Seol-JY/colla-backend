package one.colla.common.presentation;

import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import one.colla.global.exception.CommonException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	public static final int SUCCESS_CODE = 20000;

	private final int code;
	private final T content;
	private final String message;

	public static <T> ApiResponse<T> createSuccessResponse(T content) {
		return new ApiResponse<>(SUCCESS_CODE, content, null);
	}

	public static <T> ApiResponse<T> createErrorResponse(CommonException ex) {
		return new ApiResponse<>(ex.getErrorCode(), null, ex.getMessage());
	}

	public static ResponseEntity<ApiResponse<?>> createErrorResponseEntity(CommonException ex) {
		return ResponseEntity
			.status(ex.getHttpStatus())
			.body(createErrorResponse(ex));
	}
}
