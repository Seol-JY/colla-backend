package one.colla.common.util;

import java.util.Base64;
import java.util.Map;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;

@Component
public class TokenParser {

	private static final int JWT_SECTION_COUNT = 3;
	private static final int JWT_PAYLOAD_INDEX = 1;

	public Map<String, Object> parseIdTokenPayload(String idToken) {

		String[] jwtSections = idToken.split("\\.");

		if (jwtSections.length != JWT_SECTION_COUNT) {
			throw new IllegalArgumentException("JWT는 헤더, 페이로드, 시그니처의 세 부분으로 구성되어야 합니다.");
		}

		String payload = jwtSections[JWT_PAYLOAD_INDEX];

		String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));
		return new JacksonJsonParser().parseMap(decodedPayload);
	}
}
