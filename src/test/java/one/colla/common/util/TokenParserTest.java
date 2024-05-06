package one.colla.common.util;

import static org.assertj.core.api.Assertions.*;

import java.util.Base64;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TokenParserTest {

	private final TokenParser tokenParser = new TokenParser();

	@Test
	@DisplayName("정상적인 ID 토큰 페이로드 파싱")
	public void testParseValidIdTokenPayload() {

		// given
		String dummyPayload = "{\"email\":\"test@example.com\",\"name\":\"Test User\"}";
		String encodedPayload = Base64.getUrlEncoder().encodeToString(dummyPayload.getBytes());
		String dummyToken = "header." + encodedPayload + ".signature";

		// when
		Map<String, Object> parsedPayload = tokenParser.parseIdTokenPayload(dummyToken);

		// then
		assertThat(parsedPayload).isNotNull();
		assertThat(parsedPayload).containsEntry("email", "test@example.com");
		assertThat(parsedPayload).containsEntry("name", "Test User");
	}

	@Test
	@DisplayName("잘못된 형식의 ID 토큰 처리")
	public void testParseMalformedIdToken() {

		// given
		String inValidIdToken = "header.signature";

		// when & then
		assertThatThrownBy(() -> tokenParser.parseIdTokenPayload(inValidIdToken))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("JWT는 헤더, 페이로드, 시그니처의 세 부분으로 구성되어야 합니다.");
	}

}
