package one.colla.feed.common.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import one.colla.feed.common.application.CommentService;
import one.colla.feed.common.application.dto.request.CreateCommentRequest;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends ControllerTest {

	@MockBean
	private CommentService commentService;

	@Nested
	@DisplayName("댓글 생성 문서화")
	class CreateCommentDocs {
		Long teamspaceId = 1L;
		Long feedId = 1L;
		CreateCommentRequest request;

		@DisplayName("댓글 생성 성공")
		@WithMockCustomUser
		@Test
		void createCommentSuccessfully() throws Exception {
			request = new CreateCommentRequest("테스트 댓글 내용");

			willDoNothing().given(commentService)
				.create(any(CustomUserDetails.class), eq(teamspaceId), eq(feedId), any(CreateCommentRequest.class));

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
					RestDocumentationRequestBuilders.post("/api/v1/teamspaces/{teamspaceId}/feeds/{feedId}/comments",
							teamspaceId, feedId).with(csrf())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("comment-feed-controller")
						.description("피드 댓글을 작성합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID"),
							parameterWithName("feedId").description("피드 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("CreateCommentRequest"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("댓글 수정 문서화")
	class PatchCommentDocs {
		Long teamspaceId = 1L;
		Long feedId = 1L;
		Long commentId = 1L;
		CreateCommentRequest request;

		@DisplayName("댓글 수정 성공")
		@WithMockCustomUser
		@Test
		void patchCommentSuccessfully() throws Exception {
			request = new CreateCommentRequest("수정된 댓글 내용");

			willDoNothing().given(commentService)
				.update(any(CustomUserDetails.class), eq(teamspaceId), eq(feedId), eq(commentId),
					any(CreateCommentRequest.class));

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
					RestDocumentationRequestBuilders.patch(
							"/api/v1/teamspaces/{teamspaceId}/feeds/{feedId}/comments/{commentId}",
							teamspaceId, feedId, commentId).with(csrf())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("comment-feed-controller")
						.description("피드 댓글을 수정합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID"),
							parameterWithName("feedId").description("피드 ID"),
							parameterWithName("commentId").description("댓글 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("CreateCommentRequest"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("댓글 삭제 문서화")
	class DeleteCommentDocs {
		Long teamspaceId = 1L;
		Long feedId = 1L;
		Long commentId = 1L;

		@DisplayName("댓글 삭제 성공")
		@WithMockCustomUser
		@Test
		void deleteCommentSuccessfully() throws Exception {
			willDoNothing().given(commentService)
				.delete(any(CustomUserDetails.class), eq(teamspaceId), eq(feedId), eq(commentId));

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
					RestDocumentationRequestBuilders.delete(
							"/api/v1/teamspaces/{teamspaceId}/feeds/{feedId}/comments/{commentId}",
							teamspaceId, feedId, commentId).with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("comment-feed-controller")
						.description("피드 댓글을 삭제합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID"),
							parameterWithName("feedId").description("피드 ID"),
							parameterWithName("commentId").description("댓글 ID")
						)
						.responseFields(responseFields)
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}
}
