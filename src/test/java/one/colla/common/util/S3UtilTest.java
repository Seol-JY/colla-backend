package one.colla.common.util;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import one.colla.common.CommonTest;
import one.colla.common.application.dto.request.DomainType;

class S3UtilTest extends CommonTest {

	private static String DELIMITER = "/";

	@Autowired
	private S3Util s3Util;

	@Value("${cloud.aws.s3.endpoint}")
	private String endPoint;

	Long userId;
	Long teamspaceId;

	@BeforeEach
	void setup() {
		userId = 1L;
		teamspaceId = 1L;
	}

	static Stream<Arguments> objectKeyProvider() {
		return Stream.of(
			arguments(DomainType.USER, "users/", "_test.pdf"),
			arguments(DomainType.TEAMSPACE, "teamspaces/", "_test.pdf")
		);
	}

	@ParameterizedTest
	@MethodSource("objectKeyProvider")
	@DisplayName("도메인 타입에 해당하는 object key를 만들 수 있다.")
	void testCreateObjectKey(DomainType domainType, String expectedStart, String expectedContain) {

		// given
		String originalFileName = "test.pdf";

		// when
		String result = s3Util.createObjectKey(domainType, teamspaceId, originalFileName, userId);

		// then
		assertThat(result).startsWith(expectedStart);
		assertThat(result).contains(expectedContain);
	}

	@Test
	@DisplayName("실제 저장되는 파일 url을 만들 수 있다.")
	void testCreateAttachmentUrl() {
		// given
		String objectKey = "teamspaces/1/users/1/a3f77c12-da97-4871-8126-4c200749ea80_teamspace.txt";

		// when
		String url = s3Util.createAttachmentUrl(objectKey);

		// then
		assertThat(url).isEqualTo(endPoint + DELIMITER + objectKey);

	}

}
