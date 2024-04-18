package one.colla.common.presentation;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

class ApiResponseTest {

	private static final int SUCCESS_CODE = 20000;

	@DisplayName("성공 응답을 생성하면 code값은 20000이고, content는 주어진 값이며, message는 null이다.")
	@Test
	void createSuccessResponse() {

		// given
		Object content = Map.of();

		// when
		ApiResponse<?> response = ApiResponse.createSuccessResponse(content);

		// then
		assertThat(getField(response, "code"))
			.isEqualTo(SUCCESS_CODE);

		assertThat(getField(response, "content"))
			.isEqualTo(content);

		assertThat(getField(response, "message"))
			.isNull();
	}

	@DisplayName("에러 응답을 생성하면 code는 에러 코드이고, content는 null이며, message는 에러 메시지이다.")
	@Test
	void createErrorResponse() {

		// given
		ExceptionCode serverError = ExceptionCode.UNEXPECTED_ERROR;
		CommonException ex = new CommonException(serverError);

		// when
		ApiResponse<?> response = ApiResponse.createErrorResponse(ex);

		// then
		assertThat(getField(response, "code"))
			.isEqualTo(serverError.getErrorCode());

		assertThat(getField(response, "content"))
			.isNull();

		assertThat(getField(response, "message"))
			.isEqualTo(serverError.getMessage());
	}

	@DisplayName("에러 응답 엔티티 생성 시, HTTP 상태 코드와 에러 메시지가 적절하게 반환된다.")
	@Test
	void createErrorResponseEntityUsingReflection() {
		// given
		ExceptionCode serverError = ExceptionCode.UNEXPECTED_ERROR;
		CommonException ex = new CommonException(serverError);

		// when
		ResponseEntity<ApiResponse<?>> responseEntity = ApiResponse.createErrorResponseEntity(ex);

		// then
		assertThat(responseEntity.getStatusCode())
			.isEqualTo(serverError.getHttpStatus());

		assertThat(responseEntity.getBody())
			.isNotNull();

		assertThat(getField(responseEntity.getBody(), "code"))
			.isEqualTo(serverError.getErrorCode());

		assertThat(getField(responseEntity.getBody(), "content"))
			.isNull();

		assertThat(getField(responseEntity.getBody(), "message"))
			.isEqualTo(serverError.getMessage());
	}
}
