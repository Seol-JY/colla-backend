package one.colla.common.presentation;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

class ApiResponseTest {

	private static final int SUCCESS_CODE = 20000;

	@DisplayName("성공 응답을 생성하면 code값은 20000이고, content는 주어진 값이며, message는 null이다.")
	@Test
	void createSuccessResponse() {

		//given
		Object content = Map.of();

		//when
		ApiResponse<?> response = ApiResponse.createSuccessResponse(content);

		//then
		assertThat(response.getCode()).isEqualTo(SUCCESS_CODE);
		assertThat(response.getContent()).isEqualTo(content);
		assertThat(response.getMessage()).isEqualTo(null);
	}

	@Test
	@DisplayName("에러 응답을 생성하면 code는 에러 코드이고, content는 null이며, message는 에러 메시지이다.")
	void createErrorResponse() {

		// given
		ExceptionCode serverError = ExceptionCode.UNEXPECTED_ERROR;
		CommonException ex = new CommonException(serverError);

		// when
		ApiResponse<?> response = ApiResponse.createErrorResponse(ex);

		// then
		assertThat(response.getCode()).isEqualTo(serverError.getErrorCode());
		assertThat(response.getContent()).isEqualTo(null);
		assertThat(response.getMessage()).isEqualTo(serverError.getMessage());
	}
}
