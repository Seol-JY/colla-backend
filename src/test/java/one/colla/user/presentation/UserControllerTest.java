package one.colla.user.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import one.colla.common.ControllerTest;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.security.authentication.WithMockCustomUser;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.teamspace.domain.vo.TeamspaceProfileImageUrl;
import one.colla.user.application.UserService;
import one.colla.user.application.dto.request.LastSeenUpdateRequest;
import one.colla.user.application.dto.response.ParticipatedTeamspaceDto;
import one.colla.user.application.dto.response.ProfileDto;
import one.colla.user.application.dto.response.UserStatusResponse;
import one.colla.user.domain.User;
import one.colla.user.domain.vo.UserProfileImageUrl;

@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTest {

	@MockBean
	UserService userservice;

	@Nested
	@DisplayName("팀스페이스 태그 생성 문서화")
	class getUserStatusDocs {
		final User user = USER1();
		final Teamspace osTeamspace = OS_TEAMSPACE();
		final Teamspace dbTeamspace = DATABASE_TEAMSPACE();
		final String USER_PROFILE_IMAGE_URL = "http://user-profile-image.com";
		final String OS_TEAMSPACE_PROFILE_IMAGE_URL = "http://os-profile-image.com";
		final String DB_TEAMSPACE_PROFILE_IMAGE_URL = "http://db-profile-image.com";
		final Long userId = 1L;
		final Long osTeamspaceId = 1L;
		final Long dbTeamspaceId = 2L;
		final Long lastSeenTeamspaceId = 2L;
		final int numOfOsTeamspaceParticipants = 5;
		final int numOfDbTeamspaceParticipants = 4;

		@Test
		@DisplayName("사용자 프로필 및 팀스페이스 참여 세부 사항 문서화")
		@WithMockCustomUser
		void getUserStatus() throws Exception {
			user.updateProfileImage(new UserProfileImageUrl(USER_PROFILE_IMAGE_URL));
			osTeamspace.changeProfileImageUrl(new TeamspaceProfileImageUrl(OS_TEAMSPACE_PROFILE_IMAGE_URL));
			dbTeamspace.changeProfileImageUrl(new TeamspaceProfileImageUrl(DB_TEAMSPACE_PROFILE_IMAGE_URL));

			final UserTeamspace osUserTeamspace = MEMBER_USERTEAMSPACE(user, osTeamspace);
			final UserTeamspace dbUserTeamspace = MEMBER_USERTEAMSPACE(user, dbTeamspace);
			final ProfileDto profile = ProfileDto.of(userId, user, lastSeenTeamspaceId);
			final List<ParticipatedTeamspaceDto> participatedTeamspaceDto = List.of(
				ParticipatedTeamspaceDto.of(osTeamspaceId, osUserTeamspace, numOfOsTeamspaceParticipants),
				ParticipatedTeamspaceDto.of(dbTeamspaceId, dbUserTeamspace, numOfDbTeamspaceParticipants)
			);
			UserStatusResponse userStatusResponse = UserStatusResponse.of(profile, participatedTeamspaceDto);

			given(userservice.getUserStatus(any(CustomUserDetails.class))).willReturn(userStatusResponse);

			doTest(
				ApiResponse.createSuccessResponse(userStatusResponse),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(
					fieldWithPath("profile.userId").description("사용자 ID")
						.type(JsonFieldType.NUMBER),
					fieldWithPath("profile.username").description("사용자 이름")
						.type(JsonFieldType.STRING),
					fieldWithPath("profile.profileImageUrl").description("프로필 이미지 URL")
						.type(JsonFieldType.STRING),
					fieldWithPath("profile.email").description("이메일 주소")
						.type(JsonFieldType.STRING),
					fieldWithPath("profile.emailSubscription").description("이메일 구독 여부")
						.type(JsonFieldType.BOOLEAN),
					fieldWithPath("profile.commentNotification").description("댓글 알림 설정")
						.type(JsonFieldType.STRING),
					fieldWithPath("profile.lastSeenTeamspaceId").description("마지막으로 방문한 팀스페이스 ID")
						.type(JsonFieldType.NUMBER),
					fieldWithPath("participatedTeamspaces[].teamspaceId").description("팀스페이스 ID")
						.type(JsonFieldType.NUMBER),
					fieldWithPath("participatedTeamspaces[].name").description("팀스페이스 이름")
						.type(JsonFieldType.STRING),
					fieldWithPath("participatedTeamspaces[].profileImageUrl").description("팀스페이스 프로필 이미지 URL")
						.type(JsonFieldType.STRING),
					fieldWithPath("participatedTeamspaces[].teamspaceRole").description("팀스페이스에서의 역할")
						.type(JsonFieldType.STRING),
					fieldWithPath("participatedTeamspaces[].numOfParticipants").description("팀스페이스 참여 인원 수")
						.type(JsonFieldType.NUMBER)
				),
				"ApiResponse<UserStatusResponse>"
			);
		}

		private void doTest(
			ApiResponse<?> response,
			ResultMatcher statusMatcher,
			FieldDescriptor[] responseFields,
			String responseSchemaTitle
		) throws Exception {
			mockMvc.perform(get("/api/v1/users/status")
					.with(csrf()))
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("user-controller")
						.description("사용자 프로필과 팀스페이스 참여 세부사항을 조회합니다.")
						.responseFields(responseFields)
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)))
				.andDo(print());

		}

	}

	@Nested
	@DisplayName("마지막으로 본 팀스페이스 업데이트 문서화")
	class UpdateLastSeenDoc {
		Long teamSpaceId = 1L;
		LastSeenUpdateRequest request = new LastSeenUpdateRequest(teamSpaceId);

		@Test
		@DisplayName("마지막으로 본 팀스페이스 Id를 업데이트 할 수 있다.")
		@WithMockCustomUser
		void updateLastSeenTeamspace_Success() throws Exception {
			willDoNothing().given(userservice).updateLastSeenTeamspace(any(CustomUserDetails.class), eq(request));
			doTest(
				ApiResponse.createSuccessResponse(Map.of()),
				status().isOk(),
				apiDocHelper.createSuccessResponseFields(),
				"ApiResponse"
			);

		}

		@Test
		@DisplayName("참여하지 않은 팀스페이스 Id로 업데이트 할 수 없다.")
		@WithMockCustomUser
		void updateLastSeenTeamspace_Fail() throws Exception {
			willThrow(new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE)).given(userservice)
				.updateLastSeenTeamspace(any(CustomUserDetails.class), eq(request));

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
			mockMvc.perform(post("/api/v1/users/last-seen")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.with(csrf()))
				.andExpect(statusMatcher)
				.andExpect(content().json(objectMapper.writeValueAsString(response)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("user-controller")
						.description("사용자가 마지막으로 본 팀스페이스 Id를 업데이트 합니다.")
						.responseFields(responseFields)
						.responseSchema(Schema.schema(responseSchemaTitle))
						.build()
					)))
				.andDo(print());

		}

	}

}
