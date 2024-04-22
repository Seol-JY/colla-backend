package one.colla.global.exception;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionCodeTest {
	@Test
	@DisplayName("ErrorCode 는 중복되지 않는다.")
	void testUniqueErrorCode() {
		// given when
		Set<Integer> codes = new HashSet<>();
		for (ExceptionCode code : ExceptionCode.values()) {
			codes.add(code.getErrorCode());
		}

		assertThat(codes).hasSize(ExceptionCode.values().length);
	}

	@Test
	@DisplayName("Message 는 중복되지 않는다.")
	void testUniqueMessage() {
		// given when
		Set<String> messages = new HashSet<>();
		for (ExceptionCode code : ExceptionCode.values()) {
			messages.add(code.getMessage());
		}

		assertThat(messages).hasSize(ExceptionCode.values().length);
	}
}
