package one.colla.chat.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import one.colla.chat.application.ChatChannelService;
import one.colla.chat.application.dto.request.CreateChatChannelRequest;
import one.colla.chat.application.dto.response.CreateChatChannelResponse;
import one.colla.common.ControllerTest;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.security.authentication.WithMockCustomUser;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@WebMvcTest(ChatController.class)
class ChatControllerTest extends ControllerTest {

	@MockBean
	private ChatChannelService chatChannelService;

	@Nested
	@DisplayName("채팅 채널 생성 문서화")
	class CreateChatChannelDocs {
		final Long teamspaceId = 1L;
		final CreateChatChannelRequest request = new CreateChatChannelRequest("새로운 채팅 채널");
		final CreateChatChannelResponse response = new CreateChatChannelResponse(1L);

		@DisplayName("채팅 채널 생성 성공")
		@WithMockCustomUser
		@Test
		void createChatChannel_Success() throws Exception {

			given(chatChannelService.createChatChannel(any(CustomUserDetails.class), eq(teamspaceId), eq(request)))
				.willReturn(response);

			doTest(
				ApiResponse.createSuccessResponse(response),
				status().isCreated(),
				apiDocHelper.createSuccessResponseFields(
					fieldWithPath("chatChannelId").description("생성된 채팅 채널 Id")
				),
				"ApiResponse<CreateChatChannelResponse>"
			);
		}

		@DisplayName("채팅 채널 생성 실패 - 접근 권한이 없거나 존재하지 않는 팀스페이스")
		@WithMockCustomUser
		@Test
		void createChatChannel_Fail() throws Exception {
			willThrow(new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE)).given(chatChannelService)
				.createChatChannel(any(CustomUserDetails.class), eq(teamspaceId), eq(request));

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
					post("/api/v1/teamspaces/{teamspaceId}/chat-channels", teamspaceId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.with(csrf()))
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("chatChannel-controller")
						.description("채팅 채널을 생성합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("CreateChatChannelRequest"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());

		}
	}

}
