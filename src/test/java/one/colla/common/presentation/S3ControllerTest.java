package one.colla.common.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import one.colla.common.ControllerTest;
import one.colla.common.application.FileService;
import one.colla.common.application.dto.request.DomainType;
import one.colla.common.application.dto.request.FileUploadDto;
import one.colla.common.application.dto.request.PreSignedUrlRequest;
import one.colla.common.application.dto.response.FileUploadUrlsDto;
import one.colla.common.application.dto.response.PreSignedUrlResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.security.authentication.WithMockCustomUser;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@WebMvcTest(S3Controller.class)
class S3ControllerTest extends ControllerTest {

	@MockBean
	private FileService fileService;

	Long USER1_ID;
	CustomUserDetails USER1_DETAILS;

	@BeforeEach
	void setup() {
		USER1_ID = 1L;
		USER1_DETAILS = createCustomUserDetailsByUserId(USER1_ID);
	}

	@Nested
	@DisplayName("Presigned URL 생성시")
	class PresignedUrlDocs {
		Long teamspaceId = 1L;
		PreSignedUrlRequest preSignedUrlRequest = new PreSignedUrlRequest(List.of(
			new FileUploadDto(DomainType.TEAMSPACE, teamspaceId, "profile.jpg"))
		);

		@Test
		@DisplayName("Presigned URL 발급 성공")
		@WithMockCustomUser
		void getPresignedUrls_Success() throws Exception {

			URL presignedUrl = new URL("https://presigned-url.com");
			String attachmentUrl = "https://attachment-url.com";

			List<FileUploadUrlsDto> fileUploadUrlsDtos = List.of(new FileUploadUrlsDto(presignedUrl, attachmentUrl));
			PreSignedUrlResponse response = PreSignedUrlResponse.from(fileUploadUrlsDtos);

			given(fileService.getPresignedUrl(eq(preSignedUrlRequest), any(CustomUserDetails.class))).willReturn(
				response);

			doTest(
				ApiResponse.createSuccessResponse(response),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(
					fieldWithPath("fileUploadUrlsDtos[].presignedUrl")
						.description("생성된 presigned URL")
						.type(JsonFieldType.STRING),
					fieldWithPath("fileUploadUrlsDtos[].attachmentUrl")
						.description("생성된 파일 URL")
						.type(JsonFieldType.STRING)
				),
				"ApiResponse<PreSignedUrlResponse>"
			);

		}

		@Test
		@DisplayName("요청 팀스페이스에 참가하지 않은 사용자가 요청시 Forbidden 예외가 발생한다.")
		@WithMockCustomUser
		void getPresignedUrls_Fail() throws Exception {
			willThrow(new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE))
				.given(fileService).getPresignedUrl(eq(preSignedUrlRequest), any(CustomUserDetails.class));

			doTest(
				ApiResponse.createErrorResponse(ExceptionCode.FORBIDDEN_TEAMSPACE),
				status().isForbidden(),
				apiDocHelper.createErrorResponseFields(),
				"ApiResponse"
			);

		}

		private void doTest(
			ApiResponse<?> response,
			ResultMatcher statusMatcher,
			FieldDescriptor[] responseFields,
			String responseSchemaTitle
		) throws Exception {

			mockMvc.perform(
					post("/api/v1/presigned")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(preSignedUrlRequest))
						.with(csrf()))
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("s3-controller")
						.description("Presigned URL, 저장될 파일 주소를 생성합니다.")
						.requestFields(
							fieldWithPath("fileUploadDtos[].domainType")
								.description("도메인 타입 [USER, TEAMSPACE]")
								.type(JsonFieldType.STRING),
							fieldWithPath("fileUploadDtos[].teamspaceId")
								.description("팀스페이스 ID [도메인 타입이 teamspace일 경우만 요청 O, user 타입일 경우 요청 X]")
								.type(JsonFieldType.NUMBER),
							fieldWithPath("fileUploadDtos[].originalAttachmentName")
								.description("파일 이름 (확장자 포함)")
								.type(JsonFieldType.STRING)

						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("PreSignedUrlRequest"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

}
