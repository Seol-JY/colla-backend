package one.colla.feed.normal.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultMatcher;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import one.colla.common.ControllerTest;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.security.authentication.WithMockCustomUser;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.normal.application.NormalFeedService;
import one.colla.feed.normal.application.dto.request.CreateNormalFeedDetails;

@WebMvcTest(NormalFeedController.class)
class NormalFeedControllerTest extends ControllerTest {

	@MockBean
	private NormalFeedService normalFeedService;

	@Nested
	@DisplayName("일반 피드 작성 문서화")
	class PostNormalFeedDocs {
		Long teamspaceId = 1L;
		CommonCreateFeedRequest<CreateNormalFeedDetails> request;

		@DisplayName("댓글 생성 성공")
		@WithMockCustomUser
		@Test
		void postNormalFeedSuccessfully() throws Exception {

			List<CommonCreateFeedRequest.FileDto> images = List.of(
				new CommonCreateFeedRequest.FileDto("이미지 제목", "https://cdn.colla.so/example", 123L)
			);
			List<CommonCreateFeedRequest.FileDto> attachments = List.of(
				new CommonCreateFeedRequest.FileDto("파일 제목", "https://cdn.colla.so/example", 123L)
			);
			CreateNormalFeedDetails details = new CreateNormalFeedDetails("일반 게시글 내용");

			request = new CommonCreateFeedRequest<>("피드 제목", images, attachments, details);

			willDoNothing().given(normalFeedService)
				.create(any(CustomUserDetails.class), eq(teamspaceId), any());

			doTest(
				ApiResponse.createSuccessResponse(Map.of()),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(),
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
					RestDocumentationRequestBuilders.post("/api/v1/teamspaces/{teamspaceId}/feeds/normal",
							teamspaceId).with(csrf())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("normal-feed-controller")
						.description("일반 피드를 작성합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("CommonCreateFeedRequest<CreateNormalFeedDetails>"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}
}
