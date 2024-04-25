package one.colla.common.presentation;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.global.exception.VoException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponse<T> {

	public static final int SUCCESS_CODE = 20000;
	public static final int VALIDATION_ERROR_CODE = 30001;
	public static final int VO_ERROR_CODE = 30002;
	public static final int SERVER_ERROR_CODE = 50001;
	public static final String VO_ERROR_MESSAGE = "데이터 형식이 유효하지 않습니다.";
	public static final String VALIDATION_ERROR_MESSAGE = "잘못된 요청 형식입니다.";
	public static final String SERVER_ERROR_MESSAGE = "서버 오류입니다.";

	private final int code;
	private final T content;
	private final String message;

	public static <T> ApiResponse<T> createSuccessResponse(T content) {
		return new ApiResponse<>(SUCCESS_CODE, content, null);
	}

	public static ApiResponse<Map<String, String>> createValidationResponse(Map<String, String> errors) {
		return new ApiResponse<>(VALIDATION_ERROR_CODE, errors, VALIDATION_ERROR_MESSAGE);
	}

	public static ApiResponse<String> createVoErrorResponse(VoException ex) {
		return new ApiResponse<>(VO_ERROR_CODE, ex.getMessage(), VO_ERROR_MESSAGE);
	}

	public static <T> ApiResponse<T> createErrorResponse(ExceptionCode ec) {
		return new ApiResponse<>(ec.getErrorCode(), null, ec.getMessage());
	}

	public static <T> ApiResponse<T> createErrorResponse(CommonException ex) {
		return new ApiResponse<>(ex.getErrorCode(), null, ex.getMessage());
	}

	public static <T> ApiResponse<T> createServerErrorResponse() {
		return new ApiResponse<>(SERVER_ERROR_CODE, null, SERVER_ERROR_MESSAGE);
	}

	public static ResponseEntity<ApiResponse<Map<String, String>>> createValidationResponseEntity(
		Map<String, String> errors) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(createValidationResponse(errors));
	}

	public static <T> ResponseEntity<ApiResponse<T>> createErrorResponseEntity(CommonException ex) {
		return ResponseEntity
			.status(ex.getHttpStatus())
			.body(createErrorResponse(ex));
	}

	public static <T> ResponseEntity<ApiResponse<T>> createErrorResponseEntity(ExceptionCode ex) {
		return ResponseEntity
			.status(ex.getHttpStatus())
			.body(createErrorResponse(ex));
	}

	public static ResponseEntity<ApiResponse<String>> createErrorResponseEntity(VoException ex) {
		return ResponseEntity
			.status(HttpStatus.UNPROCESSABLE_ENTITY)
			.body(createVoErrorResponse(ex));
	}

	public static <T> ResponseEntity<ApiResponse<T>> createServerErrorResponseEntity() {
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(createServerErrorResponse());
	}

}
