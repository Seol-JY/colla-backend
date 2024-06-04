package one.colla.feed.scheduling.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
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
import one.colla.feed.scheduling.application.SchedulingFeedService;
import one.colla.feed.scheduling.application.dto.request.CreateSchedulingFeedDetails;
import one.colla.feed.scheduling.application.dto.request.PutSchedulingAvailabilitiesRequest;

@WebMvcTest(SchedulingFeedController.class)
class SchedulingFeedControllerTest extends ControllerTest {
	@MockBean
	private SchedulingFeedService schedulingFeedService;

	@Nested
	@DisplayName("일정조율 피드 작성 문서화")
	class PostSchedulingFeedDocs {
		Long teamspaceId = 1L;
		CommonCreateFeedRequest<CreateSchedulingFeedDetails> request;

		@DisplayName("일정조율 작성 성공")
		@WithMockCustomUser
		@Test
		void postSchedulingFeedSuccessfully() throws Exception {
			CreateSchedulingFeedDetails details = new CreateSchedulingFeedDetails(
				LocalDateTime.of(9999, 1, 1, 0, 0, 0),
				(byte)0,
				(byte)47,
				List.of(LocalDate.of(9999, 1, 1))
			);
			request = new CommonCreateFeedRequest<>("피드 제목", null, null, details);

			willDoNothing().given(schedulingFeedService)
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
					RestDocumentationRequestBuilders.post("/api/v1/teamspaces/{teamspaceId}/feeds/scheduling",
							teamspaceId).with(csrf())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("scheduling-feed-controller")
						.description("일정 조율 피드를 작성합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("CommonCreateFeedRequest<CreateSchedulingFeedDetails>"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("일정조율 응답 작성 및 수정 문서화")
	class PutSchedulingAvailabilityDocs {
		Long teamspaceId = 1L;
		Long feedId = 1L;
		PutSchedulingAvailabilitiesRequest request;

		@DisplayName("일정조율 응답 수정 성공")
		@WithMockCustomUser
		@Test
		void putSchedulingAvailabilitySuccessfully() throws Exception {
			Map<LocalDate, byte[]> availabilities = new HashMap<>();

			byte[] june5Array = new byte[48];
			for (int i = 0; i < 48; i++) {
				june5Array[i] = 0;
			}

			byte[] june6Array = new byte[48];
			for (int i = 0; i < 48; i++) {
				june6Array[i] = 1;
			}

			availabilities.put(LocalDate.of(9999, 6, 5), june5Array);
			availabilities.put(LocalDate.of(9999, 6, 6), june6Array);
			request = new PutSchedulingAvailabilitiesRequest(availabilities);

			willDoNothing().given(schedulingFeedService)
				.updateSchedulingAvailability(
					any(CustomUserDetails.class),
					eq(teamspaceId),
					eq(feedId),
					any(PutSchedulingAvailabilitiesRequest.class)
				);

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
					RestDocumentationRequestBuilders.put(
							"/api/v1/teamspaces/{teamspaceId}/feeds/scheduling/{feedId}/availabilities",
							teamspaceId, feedId).with(csrf())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("scheduling-feed-controller")
						.description("일정 조율 피드 응답을 작성합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID"),
							parameterWithName("feedId").description("피드 ID")
						)
						.responseFields(responseFields)
						.requestSchema(Schema.schema("PutSchedulingAvailabilitiesRequest"))
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("일정조율 응답 삭제 문서화")
	class DeleteSchedulingAvailabilityDocs {
		Long teamspaceId = 1L;
		Long feedId = 1L;

		@DisplayName("일정조율 응답 삭제 성공")
		@WithMockCustomUser
		@Test
		void deleteSchedulingAvailabilitySuccessfully() throws Exception {
			willDoNothing().given(schedulingFeedService)
				.deleteSchedulingAvailability(
					any(CustomUserDetails.class),
					eq(teamspaceId),
					eq(feedId)
				);

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
							"/api/v1/teamspaces/{teamspaceId}/feeds/scheduling/{feedId}/availabilities",
							teamspaceId, feedId).with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("scheduling-feed-controller")
						.description("일정 조율 피드 응답을 삭제합니다.")
						.pathParameters(
							parameterWithName("teamspaceId").description("팀스페이스 ID"),
							parameterWithName("feedId").description("피드 ID")
						)
						.responseFields(responseFields)
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)
				)).andDo(print());

		}
	}

}
