package one.colla.common.presentation;

import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponse<T> {

	public static final int SUCCESS_CODE = 20000;

	private final int code;
	private final T content;
	private final String message;

	public static <T> ApiResponse<T> createSuccessResponse(T content) {
		return new ApiResponse<>(SUCCESS_CODE, content, null);
	}

	public static <T> ApiResponse<T> createErrorResponse(ExceptionCode ec) {
		return new ApiResponse<>(ec.getErrorCode(), null, ec.getMessage());
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
