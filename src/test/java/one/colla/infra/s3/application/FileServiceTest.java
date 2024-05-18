package one.colla.infra.s3.application;

import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.s3.AmazonS3;

import one.colla.common.ServiceTest;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.infra.s3.application.dto.request.DomainType;
import one.colla.infra.s3.application.dto.request.FileUploadDto;
import one.colla.infra.s3.application.dto.request.PreSignedUrlRequest;
import one.colla.infra.s3.application.dto.response.PreSignedUrlResponse;
import one.colla.infra.s3.util.S3Util;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

class FileServiceTest extends ServiceTest {

	private static String DELIMITER = "/";

	@Value("${cloud.aws.s3.endpoint}")
	private String endPoint;

	@Mock
	private S3Util s3Util;

	@Mock
	private AmazonS3 amazonS3;

	@Mock
	private TeamspaceService teamspaceService;

	@InjectMocks
	private FileService fileService;

	User USER1;
	CustomUserDetails USER1_DETAILS;
	Teamspace OS_TEAMSPACE;
	UserTeamspace USER1_OS_USERTEAMSPACE;

	@BeforeEach
	void setup() {
		USER1 = testFixtureBuilder.buildUser(USER1());
		USER1_DETAILS = createCustomUserDetailsByUser(USER1);
		OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
	}

	@Nested
	@DisplayName("Presigned URL 생성시")
	class PresignedCreate {

		@Test
		@DisplayName("요청 정보로 presigned URL과 첨부 URL을 만들 수 있다.")
		void getPresignedUrl() throws MalformedURLException {

			// given
			FileUploadDto fileUploadDto = new FileUploadDto(DomainType.USER, null, "profile.jpg");
			PreSignedUrlRequest preSignedUrlRequest = new PreSignedUrlRequest(List.of(fileUploadDto));

			String objectKey = "users/profile.jpg";
			given(s3Util.createObjectKey(
				fileUploadDto.domainType(),
				fileUploadDto.teamspaceId(),
				fileUploadDto.originalAttachmentName(),
				USER1_DETAILS.getUserId())
			).willReturn(objectKey);

			URL presignedUrl = new URL("https://presigned-url.com");
			given(amazonS3.generatePresignedUrl(any())).willReturn(presignedUrl);

			given(teamspaceService.getUserTeamspace(USER1_DETAILS, fileUploadDto.teamspaceId()))
				.willReturn(USER1_OS_USERTEAMSPACE);

			String attachmentUrl = endPoint + DELIMITER + objectKey;
			given(s3Util.createAttachmentUrl(objectKey)).willReturn(attachmentUrl);

			// when
			PreSignedUrlResponse response = fileService.getPresignedUrl(preSignedUrlRequest, USER1_DETAILS);

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.fileUploadUrlsDtos()).hasSize(1);
				softly.assertThat(response.fileUploadUrlsDtos().get(0).presignedUrl()).isEqualTo(presignedUrl);
				softly.assertThat(response.fileUploadUrlsDtos().get(0).attachmentUrl()).isEqualTo(attachmentUrl);
			});
		}

		@Test
		@DisplayName("요청 팀스페이스에 참가하지 않은 사용자가 요청시 presigned URL 발급에 실패한다.")
		void getPresignedUrl_Fail() throws Exception {
			// given
			FileUploadDto fileUploadDto = new FileUploadDto(DomainType.TEAMSPACE, 999L,
				"profile.jpg");
			PreSignedUrlRequest preSignedUrlRequest = new PreSignedUrlRequest(List.of(fileUploadDto));

			willThrow(new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE))
				.given(teamspaceService).getUserTeamspace(USER1_DETAILS, fileUploadDto.teamspaceId());

			// when & then
			assertThatThrownBy(() -> fileService.getPresignedUrl(preSignedUrlRequest, USER1_DETAILS))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());
		}

	}

}
