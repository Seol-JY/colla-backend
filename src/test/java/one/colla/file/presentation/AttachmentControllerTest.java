package one.colla.file.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultMatcher;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import one.colla.common.ControllerTest;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.security.authentication.WithMockCustomUser;
import one.colla.file.application.AttachmentService;
import one.colla.file.application.dto.response.AttachmentAuthorDto;
import one.colla.file.application.dto.response.AttachmentInfoDto;
import one.colla.file.application.dto.response.StorageResponse;
import one.colla.file.domain.AttachmentType;

@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest extends ControllerTest {

	@MockBean
	private AttachmentService attachmentService;

	@Nested
	@DisplayName("첨부 파일 목록 조회 문서화")
	class GetChatChannelsDocs {
		final Long teamspaceId = 1L;
		final AttachmentType type = AttachmentType.IMAGE;
		final String attachType = "jpg";
		final String username = "주나";

		final AttachmentAuthorDto author = new AttachmentAuthorDto(1L, username, "https://example.com");
		final List<AttachmentInfoDto> attachmentInfoDtos = List.of(
			new AttachmentInfoDto(1L, "filename", type, 1024L, attachType, "https://example.jpg",
				LocalDateTime.of(2024, 6, 5, 0, 23), author)
		);
		final StorageResponse response = StorageResponse.of(1024L, attachmentInfoDtos);

		@DisplayName("첨부 파일 목록 조회 성공")
		@WithMockCustomUser
		@Test
		void getAttachments_Success() throws Exception {

			given(attachmentService.getAttachments(
				any(CustomUserDetails.class), eq(teamspaceId), eq(AttachmentType.IMAGE), eq(attachType), eq(username)))
				.willReturn(response);

			doTest(
				ApiResponse.createSuccessResponse(response),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(
					fieldWithPath("totalStorageCapacity").description("팀스페이스 현재 사용중인 첨부파일 용량"),
					fieldWithPath("attachments").description("첨부파일 목록"),
					fieldWithPath("attachments[].id").description("첨부파일 ID"),
					fieldWithPath("attachments[].name").description("첨부파일 이름"),
					fieldWithPath("attachments[].type").description("첨부파일 타입 (IMAGE, FILE)"),
					fieldWithPath("attachments[].size").description("첨부파일 크기"),
					fieldWithPath("attachments[].attachType").description("첨부파일 확장자"),
					fieldWithPath("attachments[].fileUrl").description("첨부파일 주소"),
					fieldWithPath("attachments[].createdAt").description("첨부파일 생성 일시"),
					fieldWithPath("attachments[].author").description("첨부파일 소유자"),
					fieldWithPath("attachments[].author.id").description("첨부파일 소유자 ID"),
					fieldWithPath("attachments[].author.username").description("첨부파일 소유자 이름"),
					fieldWithPath("attachments[].author.profileImageUrl").description("첨부파일 소유자 프로필 이미지 URL")
				),
				"ApiResponse<StorageResponse>"
			);

		}

		private void doTest(
			ApiResponse<?> response,
			ResultMatcher statusMatcher,
			FieldDescriptor[] responseFields,
			String responseSchemaTitle
		) throws Exception {
			mockMvc.perform(
					get("/api/v1/teamspaces/{teamspaceId}/attachments", teamspaceId)
						.queryParam("type", String.valueOf(type))
						.queryParam("attach-type", attachType)
						.queryParam("username", (username))
						.with(csrf()))
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("attachment-controller")
						.description("파일 목록을 조회합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID")
						)
						.queryParameters(
							parameterWithName("type").description("조회할 파일 타입").optional(),
							parameterWithName("attach-type").description("조회할 파일 확장자").optional(),
							parameterWithName("username").description("조회할 소유자 이름").optional()
						)
						.responseFields(responseFields)
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());

		}

	}
}
