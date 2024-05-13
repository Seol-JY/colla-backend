package one.colla.user.application;

import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static one.colla.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import one.colla.common.ServiceTest;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.infra.redis.lastseen.LastSeenTeamspace;
import one.colla.infra.redis.lastseen.LastSeenTeamspaceService;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.teamspace.domain.vo.TeamspaceProfileImageUrl;
import one.colla.user.application.dto.request.LastSeenUpdateRequest;
import one.colla.user.application.dto.request.UpdateUserSettingRequest;
import one.colla.user.application.dto.response.UserStatusResponse;
import one.colla.user.domain.CommentNotification;
import one.colla.user.domain.User;
import one.colla.user.domain.vo.UserProfileImageUrl;

public class UserServiceTest extends ServiceTest {

	final String USER_PROFILE_IMAGE_URL = "http://user-profile-image.com";
	final String OS_TEAMSPACE_PROFILE_IMAGE_URL = "http://os-profile-image.com";
	final String DB_TEAMSPACE_PROFILE_IMAGE_URL = "http://db-profile-image.com";

	@MockBean
	private LastSeenTeamspaceService lastSeenTeamspaceService;

	@MockBean
	private TeamspaceService teamspaceService;

	@Autowired
	private UserService userService;

	CustomUserDetails userDetails;
	User user;
	Teamspace osTeamspace;
	Teamspace dbTeamspace;
	UserTeamspace osUserTeamspace;
	UserTeamspace dbUserTeamspace;

	@BeforeEach
	void setUp() {
		user = testFixtureBuilder.buildUser(USER1());
		user.changeProfileImageUrl(new UserProfileImageUrl(USER_PROFILE_IMAGE_URL));
		userDetails = createCustomUserDetailsByUser(user);
		osTeamspace = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		dbTeamspace = testFixtureBuilder.buildTeamspace(DATABASE_TEAMSPACE());
		osTeamspace.changeProfileImageUrl(new TeamspaceProfileImageUrl(OS_TEAMSPACE_PROFILE_IMAGE_URL));
		dbTeamspace.changeProfileImageUrl(new TeamspaceProfileImageUrl(DB_TEAMSPACE_PROFILE_IMAGE_URL));
		osUserTeamspace = testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(user, osTeamspace));
		dbUserTeamspace = testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(user, dbTeamspace));
	}

	@Nested
	@DisplayName("사용자 프로필 및 팀스페이스 참여 세부 사항 조회시")
	class GetUserStatusClass {

		@Test
		@DisplayName("유저가 존재한다면 조회에 성공한다.")
		void getUserStatus_whenUserExists_returnsUserStatus() {
			// given
			LastSeenTeamspace lastSeenTeamspace = LastSeenTeamspace.of(user.getId(), osTeamspace.getId());
			given(lastSeenTeamspaceService.findByUserId(user.getId())).willReturn(Optional.of(lastSeenTeamspace));

			// when
			UserStatusResponse userStatusResponse = userService.getUserStatus(userDetails);

			// then
			assertSoftly(softly -> {
				softly.assertThat(userStatusResponse.profile().userId()).isEqualTo(user.getId());
				softly.assertThat(userStatusResponse.profile().email()).isEqualTo(user.getEmailValue());
				softly.assertThat(userStatusResponse.profile().profileImageUrl())
					.isEqualTo(user.getProfileImageUrlValue());
				softly.assertThat(userStatusResponse.profile().emailSubscription())
					.isEqualTo(user.isEmailSubscription());
				softly.assertThat(userStatusResponse.profile().commentNotification())
					.isEqualTo(user.getCommentNotification());
				softly.assertThat(userStatusResponse.profile().lastSeenTeamspaceId())
					.isEqualTo(lastSeenTeamspace.getTeamspaceId());
				softly.assertThat(userStatusResponse.participatedTeamspaces()).hasSize(2);

			});
		}

		@Test
		@DisplayName("유저가 존재하지 않으면 조회에 실패한다.")
		void getUserStatus_whenUserNotFound_throwsException() {
			// given
			final Long NOT_EXIST_USER_ID = -1L;
			CustomUserDetails notExistedUserDetails = createCustomUserDetailsByUserId(NOT_EXIST_USER_ID);

			// when & then
			assertThatThrownBy(() -> userService.getUserStatus(notExistedUserDetails))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_USER.getMessage());

		}
	}

	@Nested
	@DisplayName("가장 최근에 접근한 팀스페이스 조회시")
	class UpdateLastSeenClass {

		@Test
		@DisplayName("유저가 해당 팀스페이스에 참여하고 있다면 가장 최근에 접근한 팀스페이스 업데이트에 성공한다.")
		void updateLastSeenTeamspace_updatesLastSeenCorrectly() {
			// given
			LastSeenUpdateRequest request = new LastSeenUpdateRequest(osTeamspace.getId());
			given(teamspaceService.getUserTeamspace(userDetails, osTeamspace.getId())).willReturn(osUserTeamspace);
			willDoNothing().given(lastSeenTeamspaceService)
				.updateLastSeenTeamspace(user.getId(), request.teamspaceId());

			// when
			userService.updateLastSeenTeamspace(userDetails, request);

			// then
			verify(lastSeenTeamspaceService).updateLastSeenTeamspace(user.getId(), request.teamspaceId());
			verify(lastSeenTeamspaceService, times(1))
				.updateLastSeenTeamspace(user.getId(), request.teamspaceId());

		}

		@Test
		@DisplayName("유저가 해당 팀스페이스에 참여하지 않았다면 업데이트 실패한다.")
		void updateLastSeenTeamspace_failsIfUserNotPartOfTeamspace() {
			// given
			final Long NOT_EXIST_TEAMSPACE_ID = -1L;

			LastSeenUpdateRequest request = new LastSeenUpdateRequest(NOT_EXIST_TEAMSPACE_ID);
			given(teamspaceService.getUserTeamspace(userDetails, NOT_EXIST_TEAMSPACE_ID))
				.willThrow(new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE));

			// when & then
			assertThatThrownBy(() -> userService.updateLastSeenTeamspace(userDetails, request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(FORBIDDEN_TEAMSPACE.getMessage());

			verify(lastSeenTeamspaceService, never()).updateLastSeenTeamspace(anyLong(), anyLong());
		}
	}

	@Nested
	@DisplayName("사용자 설정 업데이트시")
	class UpdateSettingsTest {
		final String NEW_IMAGE_URL = "https://www.example.com/image";
		final String NEW_USERNAME = "new_username";
		final Boolean NEW_EMAIL_SUBSCRIPTION = false;
		final CommentNotification NEW_COMMENT_NOTIFICATION = CommentNotification.MENTION;

		@Test
		@DisplayName("유저 정보 업데이트에 성공한다.")
		void updateSettings_successfullyUpdatesUserInfo() {
			// given
			UpdateUserSettingRequest request = new UpdateUserSettingRequest(
				NEW_IMAGE_URL,
				NEW_USERNAME,
				NEW_EMAIL_SUBSCRIPTION,
				NEW_COMMENT_NOTIFICATION
			);

			// when
			userService.updateSettings(userDetails, request);

			// then
			assertSoftly(softly -> {
				softly.assertThat(user.getProfileImageUrlValue()).isEqualTo(NEW_IMAGE_URL);
				softly.assertThat(user.getUsernameValue()).isEqualTo(NEW_USERNAME);
				softly.assertThat(user.isEmailSubscription()).isEqualTo(NEW_EMAIL_SUBSCRIPTION);
				softly.assertThat(user.getCommentNotification()).isEqualTo(NEW_COMMENT_NOTIFICATION);
			});
		}

		@Test
		@DisplayName("유저가 존재하지 않을 경우 예외가 발생한다.")
		void updateSettings_throwsExceptionWhenUserNotFound() {
			// given
			CustomUserDetails userDetails = createCustomUserDetailsByUserId(-1L);
			UpdateUserSettingRequest request = new UpdateUserSettingRequest(
				NEW_IMAGE_URL,
				NEW_USERNAME,
				NEW_EMAIL_SUBSCRIPTION,
				NEW_COMMENT_NOTIFICATION
			);

			// when then
			assertThatThrownBy(() -> userService.updateSettings(userDetails, request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(NOT_FOUND_USER.getMessage());
		}

		@Test
		@DisplayName("유저 프로필 이미지만 업데이트 할 수 있다.")
		void updateSettings_updatesOnlyProfileImage() {
			// given
			UpdateUserSettingRequest request = new UpdateUserSettingRequest(NEW_IMAGE_URL, null, null, null);

			// when
			userService.updateSettings(userDetails, request);

			// then
			assertThat(user.getProfileImageUrlValue()).isEqualTo(NEW_IMAGE_URL);
		}
	}

	@Nested
	@DisplayName("사용자 프로필 이미지 삭제 시")
	class DeleteProfileImageUrlTest {
		@Test
		@DisplayName("삭제에 성공한다.")
		void deleteProfileImageUrlSuccessfully() {
			// when
			userService.deleteProfileImageUrl(userDetails);

			// then
			assertThat(user.getProfileImageUrlValue()).isNull();
		}
	}
}
