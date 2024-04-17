package one.colla.common.presentation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import one.colla.global.exception.CommonException;

@Getter
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
}
