package one.colla.feed.collect.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import one.colla.feed.collect.application.CollectFeedService;
import one.colla.feed.collect.application.dto.request.CreateCollectFeedDetails;
import one.colla.feed.collect.application.dto.request.UpdateCollectFeedResponseRequest;
import one.colla.feed.collect.application.dto.response.ReadCollectFeedResponseResponse;
import one.colla.feed.collect.domain.CollectFeedStatus;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;

@WebMvcTest(CollectFeedController.class)
class CollectFeedControllerTest extends ControllerTest {
	@MockBean
	private CollectFeedService collectFeedService;

	@Nested
	@DisplayName("자료수집 피드 작성 문서화")
	class PostCollectFeedDocs {
		Long teamspaceId = 1L;
		CommonCreateFeedRequest<CreateCollectFeedDetails> request;

		@DisplayName("피드 작성 성공")
		@WithMockCustomUser
		@Test
		void postCollectFeedSuccessfully() throws Exception {
			CreateCollectFeedDetails details = new CreateCollectFeedDetails(
				"피드 내용",
				LocalDateTime.of(9999, 1, 1, 0, 0, 0)
			);
			request = new CommonCreateFeedRequest<>("피드 제목", null, null, details);

			willDoNothing().given(collectFeedService)
				.create(any(CustomUserDetails.class), eq(teamspaceId), any(CommonCreateFeedRequest.class));

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
					RestDocumentationRequestBuilders.post("/api/v1/teamspaces/{teamspaceId}/feeds/collect",
							teamspaceId).with(csrf())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("collect-feed-controller")
						.description("자료수집 피드를 작성합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("CommonCreateFeedRequest<CreateCollectFeedDetails>"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("자료수집 피드 응답 작성 문서화")
	class PatchCollectFeedResponseDocs {
		Long teamspaceId = 1L;
		Long feedId = 1L;
		UpdateCollectFeedResponseRequest request;

		@DisplayName("피드 응답 작성 성공")
		@WithMockCustomUser
		@Test
		void patchCollectFeedResponseSuccessfully() throws Exception {
			request = new UpdateCollectFeedResponseRequest("응답 제목", "응답 내용");

			willDoNothing().given(collectFeedService)
				.updateResponse(any(CustomUserDetails.class), eq(teamspaceId), eq(feedId),
					any(UpdateCollectFeedResponseRequest.class));

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
							"/api/v1/teamspaces/{teamspaceId}/feeds/collect/{feedId}/responses",
							teamspaceId, feedId).with(csrf())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("collect-feed-controller")
						.description("자료수집 피드를 응답을 작성 및 수정합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID"),
							parameterWithName("feedId").description("피드 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("UpdateCollectFeedResponseRequest"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("자료수집 피드 응답 조회 문서화")
	class GetCollectFeedResponseDocs {
		Long teamspaceId = 1L;
		Long feedId = 1L;
		Long userId = 1L;
		ReadCollectFeedResponseResponse response;

		@DisplayName("피드 응답 조회 성공")
		@WithMockCustomUser
		@Test
		void getCollectFeedResponseSuccessfully() throws Exception {
			ReadCollectFeedResponseResponse.TagDto tagDto = new ReadCollectFeedResponseResponse.TagDto(1L, "프론트");
			ReadCollectFeedResponseResponse.CollectResponseAuthorDto authorDto =
				new ReadCollectFeedResponseResponse.CollectResponseAuthorDto(
					1L,
					"https://cdn.colla.so/example",
					"홍길동",
					tagDto
				);

			response =
				new ReadCollectFeedResponseResponse(
					authorDto,
					"제목",
					CollectFeedStatus.COMPLETED,
					LocalDateTime.of(2021, 1, 1, 0, 0, 0),
					"내용"
				);

			given(
				collectFeedService.readResponse(any(CustomUserDetails.class),
					eq(teamspaceId),
					eq(feedId),
					eq(userId))
			).willReturn(response);

			doTest(
				ApiResponse.createSuccessResponse(response),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(
					fieldWithPath("title").description("응답 제목"),
					fieldWithPath("status").description("응답 상태"),
					fieldWithPath("updatedAt").description("수정 일시"),
					fieldWithPath("content").description("응답 내용"),
					fieldWithPath("author.id").description("작성자 ID"),
					fieldWithPath("author.profileImageUrl").description("작성자 프로필 사진"),
					fieldWithPath("author.username").description("작성자 닉네임"),
					fieldWithPath("author.tag.id").description("작성자 태그 ID"),
					fieldWithPath("author.tag.name").description("작성자 태그 이름")
				),
				"ApiResponse<ReadCollectFeedResponseResponse>"
			);
		}

		private void doTest(
			ApiResponse<?> response,
			ResultMatcher statusMatcher,
			FieldDescriptor[] responseFields,
			String responseSchemaTitle
		) throws Exception {

			mockMvc.perform(
					RestDocumentationRequestBuilders.get(
							"/api/v1/teamspaces/{teamspaceId}/feeds/collect/{feedId}/responses/users/{userId}",
							teamspaceId, feedId, userId).with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("collect-feed-controller")
						.description("자료수집 피드를 응답을 조회합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID"),
							parameterWithName("feedId").description("피드 ID"),
							parameterWithName("userId").description("조회할 자료수집 응답의 작성자 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("Empty"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}
}
