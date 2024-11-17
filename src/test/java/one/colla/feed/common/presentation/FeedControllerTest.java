package one.colla.feed.common.presentation;

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
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultMatcher;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import one.colla.common.ControllerTest;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.security.authentication.WithMockCustomUser;
import one.colla.feed.common.application.FeedService;
import one.colla.feed.common.application.dto.response.CommentDto;
import one.colla.feed.common.application.dto.response.CommonReadFeedListResponse;
import one.colla.feed.common.application.dto.response.CommonReadFeedResponse;
import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.normal.application.dto.response.ReadNormalFeedDetails;

@WebMvcTest(FeedController.class)
class FeedControllerTest extends ControllerTest {

	@MockBean
	private FeedService feedService;

	@Nested
	@DisplayName("피드 목록 조회 문서화")
	class GetFeedsDocs {
		Long teamspaceId = 1L;

		@DisplayName("목록 조회 성공")
		@WithMockCustomUser
		@Test
		void getFeedsSuccessfully() throws Exception {
			CommonReadFeedResponse.TagDto tagDto
				= new CommonReadFeedResponse.TagDto(1L, "프론트엔드");

			CommonReadFeedResponse.FeedAuthorDto feedAuthorDto = new CommonReadFeedResponse.FeedAuthorDto(
				1L,
				"https://cdn.colla.so/example",
				"홍길동",
				tagDto
			);

			ReadNormalFeedDetails readNormalFeedDetails = ReadNormalFeedDetails.from("content");
			CommentDto.CommentAuthorDto commentAuthorDto = new CommentDto.CommentAuthorDto(
				2L,
				"https://cdn.colla.so/example",
				"댓글 작성자"
			);
			List<CommentDto> comments = List.of(
				new CommentDto(1L, commentAuthorDto, "댓글 내용", LocalDateTime.of(2021, 1, 1, 0, 0, 0))
			);
			// 이미지와 첨부 파일을 추가하여 페이로드에 포함시킵니다.
			CommonReadFeedResponse.FileDto imageDto = new CommonReadFeedResponse.FileDto(
				1L, "이미지 이름", "https://cdn.colla.so/image", 1024L
			);
			CommonReadFeedResponse.FileDto attachmentDto = new CommonReadFeedResponse.FileDto(
				1L, "첨부 파일 이름", "https://cdn.colla.so/file", 2048L
			);

			List<CommonReadFeedResponse.FileDto> images = List.of(imageDto);
			List<CommonReadFeedResponse.FileDto> attachments = List.of(attachmentDto);

			CommonReadFeedResponse<ReadFeedDetails> commonReadFeedResponse
				= new CommonReadFeedResponse<>(
				FeedType.NORMAL,
				1L,
				feedAuthorDto,
				"title",
				LocalDateTime.of(2021, 1, 1, 0, 0, 0),
				readNormalFeedDetails,
				comments,
				images,
				attachments
			);

			List<CommonReadFeedResponse<ReadFeedDetails>> feedResponses = List.of(commonReadFeedResponse);
			CommonReadFeedListResponse commonReadFeedListResponse = CommonReadFeedListResponse.from(feedResponses);

			given(feedService.readFeeds(any(CustomUserDetails.class), eq(teamspaceId), any(Long.class),
				any(FeedType.class), any(Integer.class)))
				.willReturn(commonReadFeedListResponse);

			doTest(
				ApiResponse.createSuccessResponse(commonReadFeedListResponse),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(
					fieldWithPath("feeds[].feedType").description("피드 유형"),
					fieldWithPath("feeds[].feedId").description("피드 ID"),
					fieldWithPath("feeds[].author.id").description("작성자 ID"),
					fieldWithPath("feeds[].author.profileImageUrl").description("작성자 프로필 이미지 URL"),
					fieldWithPath("feeds[].author.username").description("작성자 사용자 이름"),
					fieldWithPath("feeds[].author.tag.id").description("작성자 사용자 태그 아이디"),
					fieldWithPath("feeds[].author.tag.name").description("작성자 사용자 태그 이름"),
					fieldWithPath("feeds[].title").description("피드 제목"),
					fieldWithPath("feeds[].createdAt").description("피드 생성 일시"),
					fieldWithPath("feeds[].details.content").description("피드 내용"),
					fieldWithPath("feeds[].comments[].id").description("피드 댓글 id"),
					fieldWithPath("feeds[].comments[].author.id").description("피드 댓글 작성자 id"),
					fieldWithPath("feeds[].comments[].author.profileImageUrl").description("피드 댓글 작성자 프로필 사진"),
					fieldWithPath("feeds[].comments[].author.username").description("피드 댓글 작성자 이름"),
					fieldWithPath("feeds[].comments[].content").description("피드 댓글 내용"),
					fieldWithPath("feeds[].comments[].createdAt").description("피드 댓글 작성 일시"),
					fieldWithPath("feeds[].images[].id").description("이미지 ID"),
					fieldWithPath("feeds[].images[].name").description("이미지 이름"),
					fieldWithPath("feeds[].images[].fileUrl").description("이미지 파일 URL"),
					fieldWithPath("feeds[].images[].size").description("이미지 파일 크기"),
					fieldWithPath("feeds[].attachments[].id").description("첨부 파일 ID"),
					fieldWithPath("feeds[].attachments[].name").description("첨부 파일 이름"),
					fieldWithPath("feeds[].attachments[].fileUrl").description("첨부 파일 URL"),
					fieldWithPath("feeds[].attachments[].size").description("첨부 파일 크기")
				),
				"ApiResponse<CommonReadFeedListResponse>"
			);
		}

		private void doTest(
			ApiResponse<?> response,
			ResultMatcher statusMatcher,
			FieldDescriptor[] responseFields,
			String responseSchemaTitle
		) throws Exception {
			mockMvc.perform(get("/api/v1/teamspaces/{teamspaceId}/feeds", teamspaceId)
					.with(csrf())
					.queryParam("after", "1")
					.queryParam("type", String.valueOf(FeedType.NORMAL))
					.queryParam("limit", "5")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("feed-controller-common")
						.description("피드 목록을 조회합니다.")
						.queryParameters(
							parameterWithName("after").description("페이지네이션 커서").optional(),
							parameterWithName("type").description("피드 종류 필터링").optional(),
							parameterWithName("limit").description("개수").optional()
						)
						.responseFields(responseFields)
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("피드 단건 조회 문서화")
	class GetFeedDocs {
		Long teamspaceId = 1L;

		@DisplayName("단건 조회 성공")
		@WithMockCustomUser
		@Test
		void getFeedSuccessfully() throws Exception {
			CommonReadFeedResponse.TagDto tagDto
				= new CommonReadFeedResponse.TagDto(1L, "프론트엔드");

			CommonReadFeedResponse.FeedAuthorDto feedAuthorDto = new CommonReadFeedResponse.FeedAuthorDto(
				1L,
				"https://cdn.colla.so/example",
				"홍길동",
				tagDto
			);

			ReadNormalFeedDetails readNormalFeedDetails = ReadNormalFeedDetails.from("content");
			CommentDto.CommentAuthorDto commentAuthorDto = new CommentDto.CommentAuthorDto(
				2L,
				"https://cdn.colla.so/example",
				"댓글 작성자"
			);
			List<CommentDto> comments = List.of(
				new CommentDto(1L, commentAuthorDto, "댓글 내용", LocalDateTime.of(2021, 1, 1, 0, 0, 0))
			);
			// 이미지와 첨부 파일을 추가하여 페이로드에 포함시킵니다.
			CommonReadFeedResponse.FileDto imageDto = new CommonReadFeedResponse.FileDto(
				1L, "이미지 이름", "https://cdn.colla.so/image", 1024L
			);
			CommonReadFeedResponse.FileDto attachmentDto = new CommonReadFeedResponse.FileDto(
				1L, "첨부 파일 이름", "https://cdn.colla.so/file", 2048L
			);

			List<CommonReadFeedResponse.FileDto> images = List.of(imageDto);
			List<CommonReadFeedResponse.FileDto> attachments = List.of(attachmentDto);

			CommonReadFeedResponse<ReadFeedDetails> commonReadFeedResponse
				= new CommonReadFeedResponse<>(
				FeedType.NORMAL,
				1L,
				feedAuthorDto,
				"title",
				LocalDateTime.of(2021, 1, 1, 0, 0, 0),
				readNormalFeedDetails,
				comments,
				images,
				attachments
			);

			given(feedService.readFeed(any(CustomUserDetails.class), eq(teamspaceId), eq(1L)))
				.willReturn(commonReadFeedResponse);

			doTest(
				ApiResponse.createSuccessResponse(commonReadFeedResponse),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(
					fieldWithPath("feedType").description("피드 유형"),
					fieldWithPath("feedId").description("피드 ID"),
					fieldWithPath("author.id").description("작성자 ID"),
					fieldWithPath("author.profileImageUrl").description("작성자 프로필 이미지 URL"),
					fieldWithPath("author.username").description("작성자 사용자 이름"),
					fieldWithPath("author.tag.id").description("작성자 사용자 태그 아이디"),
					fieldWithPath("author.tag.name").description("작성자 사용자 태그 이름"),
					fieldWithPath("title").description("피드 제목"),
					fieldWithPath("createdAt").description("피드 생성 일시"),
					fieldWithPath("details.content").description("피드 내용"),
					fieldWithPath("comments[].id").description("피드 댓글 id"),
					fieldWithPath("comments[].author.id").description("피드 댓글 작성자 id"),
					fieldWithPath("comments[].author.profileImageUrl").description("피드 댓글 작성자 프로필 사진"),
					fieldWithPath("comments[].author.username").description("피드 댓글 작성자 이름"),
					fieldWithPath("comments[].content").description("피드 댓글 내용"),
					fieldWithPath("comments[].createdAt").description("피드 댓글 작성 일시"),
					fieldWithPath("images[].id").description("이미지 ID"),
					fieldWithPath("images[].name").description("이미지 이름"),
					fieldWithPath("images[].fileUrl").description("이미지 파일 URL"),
					fieldWithPath("images[].size").description("이미지 파일 크기"),
					fieldWithPath("attachments[].id").description("첨부 파일 ID"),
					fieldWithPath("attachments[].name").description("첨부 파일 이름"),
					fieldWithPath("attachments[].fileUrl").description("첨부 파일 URL"),
					fieldWithPath("attachments[].size").description("첨부 파일 크기")
				),
				"ApiResponse<CommonReadFeedResponse>"
			);
		}

		private void doTest(
			ApiResponse<?> response,
			ResultMatcher statusMatcher,
			FieldDescriptor[] responseFields,
			String responseSchemaTitle
		) throws Exception {
			mockMvc.perform(get("/api/v1/teamspaces/{teamspaceId}/feeds/{feedId}", teamspaceId, 1L)
					.with(csrf())
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("feed-controller-common")
						.description("피드 단건을 조회합니다.")
						.responseFields(responseFields)
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("피드 삭제 문서화")
	class DeleteFeedsDocs {
		Long teamspaceId = 1L;
		Long feedId = 1L;

		@DisplayName("삭제 성공")
		@WithMockCustomUser
		@Test
		void deleteFeedsSuccessfully() throws Exception {
			willDoNothing().given(feedService).delete(any(CustomUserDetails.class), eq(teamspaceId), eq(feedId));
			doTest(
				ApiResponse.createSuccessResponse(Map.of()),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(
				),
				"ApiResponse"
			);
		}

		private void doTest(
			ApiResponse<?> response,
			ResultMatcher statusMatcher,
			FieldDescriptor[] responseFields,
			String responseSchemaTitle
		) throws Exception {
			mockMvc.perform(delete("/api/v1/teamspaces/{teamspaceId}/feeds/{feedId}", teamspaceId, feedId)
					.with(csrf())
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("feed-controller-common")
						.description("피드를 삭제합니다.")
						.responseFields(responseFields)
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}
}
