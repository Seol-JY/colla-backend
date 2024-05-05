package one.colla.teamspace.application;

import static one.colla.common.fixtures.TagFixtures.*;
import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import one.colla.common.ServiceTest;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.util.RandomCodeGenerator;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.infra.mail.events.InviteCodeSendMailEvent;
import one.colla.infra.redis.invite.InviteCode;
import one.colla.infra.redis.invite.InviteCodeService;
import one.colla.teamspace.application.dto.request.CreateTagRequest;
import one.colla.teamspace.application.dto.request.CreateTeamspaceRequest;
import one.colla.teamspace.application.dto.request.ParticipateRequest;
import one.colla.teamspace.application.dto.request.SendMailInviteCodeRequest;
import one.colla.teamspace.application.dto.request.UpdateTeamspaceSettingsRequest;
import one.colla.teamspace.application.dto.response.CreateTagResponse;
import one.colla.teamspace.application.dto.response.CreateTeamspaceResponse;
import one.colla.teamspace.application.dto.response.InviteCodeResponse;
import one.colla.teamspace.application.dto.response.ParticipantDto;
import one.colla.teamspace.application.dto.response.TeamspaceInfoResponse;
import one.colla.teamspace.application.dto.response.TeamspaceParticipantsResponse;
import one.colla.teamspace.application.dto.response.TeamspaceSettingsResponse;
import one.colla.teamspace.domain.Tag;
import one.colla.teamspace.domain.TagRepository;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.TeamspaceRepository;
import one.colla.teamspace.domain.TeamspaceRole;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.teamspace.domain.UserTeamspaceRepository;
import one.colla.user.domain.User;

class TeamspaceServiceTest extends ServiceTest {
	@MockBean
	private InviteCodeService inviteCodeService;

	@MockBean
	private RandomCodeGenerator randomCodeGenerator;

	@MockBean
	private ApplicationEventPublisher publisher;

	@Autowired
	private TeamspaceService teamspaceService;

	@Autowired
	private TeamspaceRepository teamspaceRepository;

	@Autowired
	private UserTeamspaceRepository userTeamspaceRepository;

	@Autowired
	private TagRepository tagRepository;

	@Nested
	@DisplayName("팀스페이스 생성시")
	class CreateTeamspaceTest {
		User USER1;
		CustomUserDetails USER1_DETAILS;
		CreateTeamspaceRequest request;
		CreateTeamspaceResponse response;

		@BeforeEach
		void setUp() {
			// given
			USER1 = testFixtureBuilder.buildUser(USER1());
			USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			request = new CreateTeamspaceRequest("새로운 팀스페이스");

			// when
			response = teamspaceService.create(USER1_DETAILS, request);
		}

		@Test
		@DisplayName("생성에 성공한다.")
		void createSuccessfully() {
			// when
			final Optional<Teamspace> savedTeamspace = teamspaceRepository.findById(response.teamspaceId());

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(savedTeamspace).hasValueSatisfying((teamspace) -> {
					assertThat(response.teamspaceId()).isEqualTo(teamspace.getId());
				});
			});
		}

		@Test
		@DisplayName("생성한 사용자는 생성된 팀스페이스의 LEADER 권한으로 참가 처리된다.")
		void createParticipatedSuccessfully() {
			// given
			final Optional<Teamspace> savedTeamspace = teamspaceRepository.findById(response.teamspaceId());

			// when
			final Optional<UserTeamspace> savedUserTeamspace = userTeamspaceRepository.findByUserIdAndTeamspaceId(
				USER1.getId(), savedTeamspace.map(Teamspace::getId).orElse(null)
			);

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(savedUserTeamspace).hasValueSatisfying((userTeamspace) -> {
					assertThat(userTeamspace.getTeamspaceRole()).isEqualTo(TeamspaceRole.LEADER);
				});
			});
		}
	}

	@Nested
	@DisplayName("초대코드를 통해 팀스페이스 정보 조회 시")
	class ReadInfoByCodeTest {
		Teamspace OS_TEAMSPACE;

		@BeforeEach
		void setUp() {
			// given
			OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		}

		@Test
		@DisplayName("로그인하지 않은 사용자가 팀스페이스 정보를 조회할 수 있고, isParticipated 는 항상 False 이다.")
		void readInfoByNotLoginedUserSuccessfully() {
			// given
			given(inviteCodeService.getTeamspaceIdByCode(any())).willReturn(OS_TEAMSPACE.getId());

			// when
			TeamspaceInfoResponse teamspaceInfoResponse = teamspaceService.readInfoByCode(null, "validInviteCode");

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(teamspaceInfoResponse).isNotNull();
				softly.assertThat(teamspaceInfoResponse.teamspaceName())
					.isEqualTo(OS_TEAMSPACE.getTeamspaceNameValue());
				softly.assertThat(teamspaceInfoResponse.teamspaceId()).isEqualTo(OS_TEAMSPACE.getId());
				softly.assertThat(teamspaceInfoResponse.teamspaceProfileImageUrl())
					.isEqualTo(OS_TEAMSPACE.getProfileImageUrlValue());
			});
			assertThat(teamspaceInfoResponse.isParticipated()).isFalse();
		}

		@Test
		@DisplayName("로그인한 사용자가 참가하지 않은 팀스페이스 초대코드로 조회하는 경우 isParticipated 는 False 이다.")
		void readInfoByLoginedUserSuccessfully() {
			// given
			User USER1 = testFixtureBuilder.buildUser(USER1());
			CustomUserDetails USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			given(inviteCodeService.getTeamspaceIdByCode(any())).willReturn(OS_TEAMSPACE.getId());

			// when
			TeamspaceInfoResponse teamspaceInfoResponse = teamspaceService.readInfoByCode(USER1_DETAILS,
				"validInviteCode");

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(teamspaceInfoResponse).isNotNull();
				softly.assertThat(teamspaceInfoResponse.teamspaceName())
					.isEqualTo(OS_TEAMSPACE.getTeamspaceNameValue());
				softly.assertThat(teamspaceInfoResponse.teamspaceId()).isEqualTo(OS_TEAMSPACE.getId());
				softly.assertThat(teamspaceInfoResponse.teamspaceProfileImageUrl())
					.isEqualTo(OS_TEAMSPACE.getProfileImageUrlValue());
			});
			assertThat(teamspaceInfoResponse.isParticipated()).isFalse();
		}

		@Test
		@DisplayName("로그인한 사용자가 이미 속한 팀스페이스 초대코드로 조회하는 경우 isParticipated 는 True 이다.")
		void readInfoByParticipantSuccessfully() {
			// given
			User USER1 = testFixtureBuilder.buildUser(USER1());
			CustomUserDetails USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			testFixtureBuilder.buildUserTeamspace(LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
			given(inviteCodeService.getTeamspaceIdByCode(any())).willReturn(OS_TEAMSPACE.getId());

			// when
			TeamspaceInfoResponse teamspaceInfoResponse = teamspaceService.readInfoByCode(USER1_DETAILS,
				"validInviteCode");

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(teamspaceInfoResponse).isNotNull();
				softly.assertThat(teamspaceInfoResponse.teamspaceName())
					.isEqualTo(OS_TEAMSPACE.getTeamspaceNameValue());
				softly.assertThat(teamspaceInfoResponse.teamspaceId()).isEqualTo(OS_TEAMSPACE.getId());
				softly.assertThat(teamspaceInfoResponse.teamspaceProfileImageUrl())
					.isEqualTo(OS_TEAMSPACE.getProfileImageUrlValue());
			});

			assertThat(teamspaceInfoResponse.isParticipated()).isTrue();
		}

		@Test
		@DisplayName("이미 만료되었거나 유효하지 않은 초대코드로 조회하는 경우 예외가 발생한다.")
		void readInfoExpierdFailure() {
			// given
			given(inviteCodeService.getTeamspaceIdByCode(any()))
				.willThrow(new CommonException(ExceptionCode.INVALID_OR_EXPIRED_INVITATION_CODE));

			// when then
			assertThatThrownBy(() -> teamspaceService.readInfoByCode(null, "validInviteCode"))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.INVALID_OR_EXPIRED_INVITATION_CODE.getMessage());
		}

		@Test
		@DisplayName("초대코드에 해당하는 팀이 존재하지 않는 경우 예외가 발생한다.")
		void readInfoNotFoundFailure() {
			// given
			final Long INVALID_TEAMSPACE_ID = -1L;
			given(inviteCodeService.getTeamspaceIdByCode(any())).willReturn(INVALID_TEAMSPACE_ID);

			// when then
			assertThatThrownBy(() -> teamspaceService.readInfoByCode(null, "validInviteCode"))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());
		}
	}

	@Nested
	@DisplayName("초대코드 발급 시")
	class GetInviteCodeTest {
		@Test
		@DisplayName("발급에 성공한다.")
		void getInviteCodeSuccessfully() {
			final String GENERATED_INVITE_CODE = "ABCDEFGHIJ";
			final int CODE_TTL = 1;
			// given
			User USER1 = testFixtureBuilder.buildUser(USER1());
			CustomUserDetails USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			Teamspace OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
			UserTeamspace USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
				LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));

			final InviteCode expectedInviteCode = InviteCode.of(GENERATED_INVITE_CODE,
				USER1_OS_USERTEAMSPACE.getTeamspace().getId(), CODE_TTL);
			given(randomCodeGenerator.generateRandomString(anyInt())).willReturn(GENERATED_INVITE_CODE);
			given(inviteCodeService.existsByCode(any())).willReturn(false);
			given(inviteCodeService.saveInviteCode(any())).willReturn(expectedInviteCode);

			// when
			InviteCodeResponse expectedInviteCodeResponse = teamspaceService.getInviteCode(USER1_DETAILS,
				OS_TEAMSPACE.getId());

			// then
			assertThat(expectedInviteCodeResponse).isEqualTo(InviteCodeResponse.from(expectedInviteCode));
		}
	}

	@Nested
	@DisplayName("초대 링크 발급 후 메일 전송 시")
	class SendInviteCodeTest {
		@Test
		@DisplayName("발급 후 메일 전송에 성공한다.")
		void sendInviteCodeSuccessfully() {
			final String GENERATED_INVITE_CODE = "ABCDEFGHIJ";
			final String EMAIL = "example@example.com";
			final int CODE_TTL = 1;
			// given
			User USER1 = testFixtureBuilder.buildUser(USER1());
			CustomUserDetails USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			Teamspace OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
			UserTeamspace USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
				LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));

			final InviteCode expectedInviteCode = InviteCode.of(GENERATED_INVITE_CODE,
				USER1_OS_USERTEAMSPACE.getTeamspace().getId(), CODE_TTL);

			given(randomCodeGenerator.generateRandomString(anyInt())).willReturn(GENERATED_INVITE_CODE);
			given(inviteCodeService.existsByCode(any())).willReturn(false);
			given(inviteCodeService.saveInviteCode(any())).willReturn(expectedInviteCode);

			ArgumentCaptor<InviteCodeSendMailEvent> argumentCaptor = ArgumentCaptor.forClass(
				InviteCodeSendMailEvent.class);

			SendMailInviteCodeRequest sendMailInviteCodeRequest = new SendMailInviteCodeRequest(EMAIL);

			// when
			teamspaceService.sendInviteCode(USER1_DETAILS, OS_TEAMSPACE.getId(), sendMailInviteCodeRequest);

			// then
			doAnswer(invocation -> {
				InviteCodeSendMailEvent capturedEvent = argumentCaptor.getValue();
				SoftAssertions.assertSoftly(softly -> {
					softly.assertThat(capturedEvent).isNotNull();
					softly.assertThat(capturedEvent.email()).isEqualTo(EMAIL);
					softly.assertThat(capturedEvent.inviteCode()).isEqualTo(expectedInviteCode);
					softly.assertThat(capturedEvent.inviterName()).isEqualTo(USER1.getUsernameValue());
					softly.assertThat(capturedEvent.teamspaceName()).isEqualTo(OS_TEAMSPACE.getTeamspaceName());
				});
				verify(publisher, times(1)).publishEvent(argumentCaptor.capture());
				return null;
			}).when(publisher).publishEvent(argumentCaptor.capture());
		}
	}

	@Nested
	@DisplayName("팀스페이스 참가 시")
	class ParticipateTest {
		final String GENERATED_INVITE_CODE = "ABCDEFGHIJ";
		User USER1;
		CustomUserDetails USER1_DETAILS;
		Teamspace OS_TEAMSPACE;
		ParticipateRequest participateRequest;

		@BeforeEach
		void setUp() {
			// given
			USER1 = testFixtureBuilder.buildUser(USER1());
			USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
			participateRequest = new ParticipateRequest(GENERATED_INVITE_CODE);
		}

		@Test
		@DisplayName("참가에 성공한다.")
		void participateSuccessfully() {
			// given
			given(inviteCodeService.getTeamspaceIdByCode(any())).willReturn(OS_TEAMSPACE.getId());

			// when
			teamspaceService.participate(USER1_DETAILS, OS_TEAMSPACE.getId(), participateRequest);

			// then
			Optional<UserTeamspace> savedUserTeamspace = userTeamspaceRepository.findByUserIdAndTeamspaceId(
				USER1_DETAILS.getUserId(), OS_TEAMSPACE.getId());

			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(savedUserTeamspace).hasValueSatisfying((userTeamspace) -> {
					assertThat(savedUserTeamspace.get().getTeamspaceRole()).isEqualTo(TeamspaceRole.MEMBER);
					assertThat(savedUserTeamspace.get().getTeamspace()).isEqualTo(OS_TEAMSPACE);
				});
			});
		}

		@Test
		@DisplayName("초대코드가 유효하나 참가 요청 팀스페이스에 대한 초대코드가 아닌 경우 예외가 발생한다.")
		void participateFailure1() {
			// given
			Teamspace DATABASE_TEAMSPACE = testFixtureBuilder.buildTeamspace(DATABASE_TEAMSPACE());
			given(inviteCodeService.getTeamspaceIdByCode(any())).willReturn(OS_TEAMSPACE.getId());

			// when then
			assertThatThrownBy(
				() -> teamspaceService.participate(USER1_DETAILS, DATABASE_TEAMSPACE.getId(), participateRequest))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.INVALID_OR_EXPIRED_INVITATION_CODE.getMessage());

		}

		@Test
		@DisplayName("이미 참가되어있는 사용자가 참가 요청을 하는 경우 예외가 발생한다.")
		void participateAlreadyFailure1() {
			// given
			given(inviteCodeService.getTeamspaceIdByCode(any())).willReturn(OS_TEAMSPACE.getId());
			testFixtureBuilder.buildUserTeamspace(LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));

			// when then
			assertThatThrownBy(
				() -> teamspaceService.participate(USER1_DETAILS, OS_TEAMSPACE.getId(), participateRequest))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.ALREADY_PARTICIPATED.getMessage());
		}

		@Test
		@DisplayName("이미 팀스페이스 인원이 가득 찬 경우 예외가 발생한다.")
		void participateFullFailure() {
			final int MAX_TEAMSPACE_USERS = 10;
			// given
			given(inviteCodeService.getTeamspaceIdByCode(any())).willReturn(OS_TEAMSPACE.getId());

			List<User> tempUsers = new ArrayList<>();
			for (int i = 0; i < MAX_TEAMSPACE_USERS; i++) {
				tempUsers.add(RANDOMUSER());
			}

			for (User user : testFixtureBuilder.buildUsers(tempUsers)) {
				testFixtureBuilder.buildUserTeamspace(user.participate(OS_TEAMSPACE, TeamspaceRole.MEMBER));
			}

			// when then
			assertThatThrownBy(
				() -> teamspaceService.participate(USER1_DETAILS, OS_TEAMSPACE.getId(), participateRequest))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.TEAMSPACE_FULL.getMessage());
		}

		@Test
		@DisplayName("이미 만료되었거나 유효하지 않은 초대코드로 참가 요청 시 예외가 발생한다.")
		void participateExpierdFailure() {
			// given
			given(inviteCodeService.getTeamspaceIdByCode(any()))
				.willThrow(new CommonException(ExceptionCode.INVALID_OR_EXPIRED_INVITATION_CODE));

			// when then
			assertThatThrownBy(
				() -> teamspaceService.participate(USER1_DETAILS, OS_TEAMSPACE.getId(), participateRequest))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.INVALID_OR_EXPIRED_INVITATION_CODE.getMessage());
		}
	}

	@Nested
	@DisplayName("팀스페이스 참가자 조회 시")
	class GetParticipantsTest {
		@Test
		@DisplayName("조회에 성공한다.")
		void getParticipantsSuccessfully() {
			// given
			User USER1 = testFixtureBuilder.buildUser(USER1());
			User USER2 = testFixtureBuilder.buildUser(USER2());

			CustomUserDetails USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			Teamspace OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());

			UserTeamspace USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
				LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
			UserTeamspace USER2_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
				MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));

			// when
			TeamspaceParticipantsResponse teamspaceParticipantsResponse
				= teamspaceService.getParticipants(USER1_DETAILS, OS_TEAMSPACE.getId());

			// then
			final ParticipantDto USER1ParticipantDto = ParticipantDto.of(USER1, USER1_OS_USERTEAMSPACE, null);
			final ParticipantDto USER2ParticipantDto = ParticipantDto.of(USER2, USER2_OS_USERTEAMSPACE, null);
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(teamspaceParticipantsResponse.users()).hasSize(2);
				softly.assertThat(teamspaceParticipantsResponse.users().get(0)).isEqualTo(USER1ParticipantDto);
				softly.assertThat(teamspaceParticipantsResponse.users().get(1)).isEqualTo(USER2ParticipantDto);
			});
		}
	}

	@Nested
	@DisplayName("팀스페이스 설정 정보 조회 시")
	class GetSettingsTest {
		User USER1, USER2;
		CustomUserDetails USER1_DETAILS;
		CustomUserDetails USER2_DETAILS;
		Teamspace OS_TEAMSPACE;
		UserTeamspace USER1_OS_USERTEAMSPACE, USER2_OS_USERTEAMSPACE;
		Tag FRONTEND_TAG, BACKEND_TAG;

		@BeforeEach
		void setUp() {
			// given
			USER1 = testFixtureBuilder.buildUser(USER1());
			USER2 = testFixtureBuilder.buildUser(USER2());

			USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			USER2_DETAILS = createCustomUserDetailsByUser(USER2);

			OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());

			USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
				LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
			USER2_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
				MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));

			FRONTEND_TAG = testFixtureBuilder.buildTag(FRONTEND_TAG(OS_TEAMSPACE));
			BACKEND_TAG = testFixtureBuilder.buildTag(BACKEND_TAG(OS_TEAMSPACE));
		}

		@Test
		@DisplayName("조회에 성공한다.")
		void getSettingsSuccessfully() {
			// when
			TeamspaceSettingsResponse teamspaceSettingsResponse = teamspaceService.getSettings(USER1_DETAILS,
				OS_TEAMSPACE.getId());

			// then
			final ParticipantDto USER1ParticipantDto = ParticipantDto.of(USER1, USER1_OS_USERTEAMSPACE, null);
			final ParticipantDto USER2ParticipantDto = ParticipantDto.of(USER2, USER2_OS_USERTEAMSPACE, null);
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(teamspaceSettingsResponse.name()).isEqualTo(OS_TEAMSPACE.getTeamspaceNameValue());
				softly.assertThat(teamspaceSettingsResponse.tags()).hasSize(2);
				softly.assertThat(teamspaceSettingsResponse.tags().get(0).name())
					.isEqualTo(FRONTEND_TAG.getTagNameValue());
				softly.assertThat(teamspaceSettingsResponse.tags().get(1).name())
					.isEqualTo(BACKEND_TAG.getTagNameValue());
				softly.assertThat(teamspaceSettingsResponse.users()).hasSize(2);
				softly.assertThat(teamspaceSettingsResponse.users().get(0)).isEqualTo(USER1ParticipantDto);
				softly.assertThat(teamspaceSettingsResponse.users().get(1)).isEqualTo(USER2ParticipantDto);
			});
		}

		@Test
		@DisplayName("관리자가 아닌 경우 예외가 발생한다.")
		void getSettingsFailure() {
			// when then
			assertThatThrownBy(
				() -> teamspaceService.getSettings(USER2_DETAILS, OS_TEAMSPACE.getId()))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.ONLY_LEADER_ACCESS.getMessage());
		}
	}

	@Nested
	@DisplayName("팀스페이스 태그 생성 시")
	class CreateTagTest {
		User USER1;
		CustomUserDetails USER1_DETAILS;
		Teamspace OS_TEAMSPACE;
		UserTeamspace USER1_OS_USERTEAMSPACE;

		@BeforeEach
		void setUp() {
			// given
			USER1 = testFixtureBuilder.buildUser(USER1());
			USER1_DETAILS = createCustomUserDetailsByUser(USER1);

			OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());

			USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
				LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
		}

		@Test
		@DisplayName("생성에 성공한다.")
		void createSuccessfully() {
			final String NEW_TAG_NAME = "프론트엔드";
			// given
			CreateTagRequest request = new CreateTagRequest(NEW_TAG_NAME);

			// when
			CreateTagResponse createTagResponse = teamspaceService.createTag(USER1_DETAILS, OS_TEAMSPACE.getId(),
				request);

			// then
			assertThat(createTagResponse.tag().name()).isEqualTo(NEW_TAG_NAME);
		}

		@Test
		@DisplayName("생성하려는 태그 이름이 이미 팀스페이스에 있다면 예외가 발생한다.")
		void createExistFailure() {
			// given
			Tag FRONTEND_TAG = testFixtureBuilder.buildTag(FRONTEND_TAG(OS_TEAMSPACE));
			CreateTagRequest request = new CreateTagRequest(FRONTEND_TAG.getTagNameValue());

			// when then
			assertThatThrownBy(
				() -> teamspaceService.createTag(USER1_DETAILS, OS_TEAMSPACE.getId(), request))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.CONFLICT_TAGS.getMessage());
		}

		@Test
		@DisplayName("관리자가 아닌 경우 예외가 발생한다.")
		void createNotLeaderFailure() {
			// given
			User USER2 = testFixtureBuilder.buildUser(USER2());
			CustomUserDetails USER2_DETAILS = createCustomUserDetailsByUser(USER2);
			testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));

			CreateTagRequest request = new CreateTagRequest("tagName");

			// when then
			assertThatThrownBy(
				() -> teamspaceService.createTag(USER2_DETAILS, OS_TEAMSPACE.getId(), request))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.ONLY_LEADER_ACCESS.getMessage());
		}
	}

	@Nested
	@DisplayName("팀스페이스 태그 생성 시")
	class UpdateSettingsTest {
		User USER1;
		CustomUserDetails USER1_DETAILS;
		Teamspace OS_TEAMSPACE;
		UserTeamspace USER1_OS_USERTEAMSPACE;
		Tag FRONTEND_TAG;

		@BeforeEach
		void setUp() {
			// given
			USER1 = testFixtureBuilder.buildUser(USER1());
			USER1_DETAILS = createCustomUserDetailsByUser(USER1);

			OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());

			USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(
				LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));

			FRONTEND_TAG = testFixtureBuilder.buildTag(FRONTEND_TAG(OS_TEAMSPACE));
		}

		@Test
		@DisplayName("팀스페이스 설정 업데이트에 성공한다.")
		void updateSettingsSuccessfully() {
			final String TEAMSPACE_NAME = "새 팀스페이스 이름";
			final String PROFILE_URL = "https://example.com";

			// given
			UpdateTeamspaceSettingsRequest request =
				new UpdateTeamspaceSettingsRequest(PROFILE_URL, TEAMSPACE_NAME,
					List.of(new UpdateTeamspaceSettingsRequest.UserUpdateInfo(USER1.getId(), FRONTEND_TAG.getId())));

			// when
			teamspaceService.updateSettings(USER1_DETAILS, OS_TEAMSPACE.getId(), request);

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(OS_TEAMSPACE.getTeamspaceNameValue()).isEqualTo(TEAMSPACE_NAME);
				softly.assertThat(OS_TEAMSPACE.getProfileImageUrlValue()).isEqualTo(PROFILE_URL);
				softly.assertThat(OS_TEAMSPACE.getUserTeamspaces().get(0).getTag().getTagName())
					.isEqualTo(FRONTEND_TAG.getTagName());
			});
		}

		@Test
		@DisplayName("팀스페이스 리더가 아닌 사용자가 설정을 업데이트할 때 예외가 발생한다.")
		void updateSettingsNotLeaderFailure() {
			final String TEAMSPACE_NAME = "새 팀스페이스 이름";
			final String PROFILE_URL = "https://example.com";

			// given
			User USER2 = testFixtureBuilder.buildUser(USER2());
			CustomUserDetails USER2_DETAILS = createCustomUserDetailsByUser(USER2);
			testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));

			UpdateTeamspaceSettingsRequest request =
				new UpdateTeamspaceSettingsRequest(null, null, null);

			// when then
			assertThatThrownBy(() -> teamspaceService.updateSettings(USER2_DETAILS, OS_TEAMSPACE.getId(), request))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.ONLY_LEADER_ACCESS.getMessage());
		}

		@Test
		@DisplayName("팀스페이스 설정 업데이트 중 태그가 존재하지 않는 경우 예외가 발생한다.")
		void updateSettingsTagNotFoundFailure() {
			final Long INVALID_TAG_ID = -1L;

			// given
			UpdateTeamspaceSettingsRequest request =
				new UpdateTeamspaceSettingsRequest(null, null,
					List.of(new UpdateTeamspaceSettingsRequest.UserUpdateInfo(USER1.getId(), INVALID_TAG_ID)));

			// when then
			assertThatThrownBy(() -> teamspaceService.updateSettings(USER1_DETAILS, OS_TEAMSPACE.getId(), request))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FAIL_CHANGE_USERTAG.getMessage());
		}
	}
}
