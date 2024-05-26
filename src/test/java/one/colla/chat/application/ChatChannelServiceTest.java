package one.colla.chat.application;

import static one.colla.common.fixtures.ChatChannelFixtures.*;
import static one.colla.common.fixtures.ChatChannelMessageFixtures.*;
import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import one.colla.chat.application.dto.request.CreateChatChannelRequest;
import one.colla.chat.application.dto.request.UpdateChatChannelNameRequest;
import one.colla.chat.application.dto.response.ChatChannelsResponse;
import one.colla.chat.application.dto.response.CreateChatChannelResponse;
import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatChannelRepository;
import one.colla.chat.domain.ChatType;
import one.colla.chat.domain.UserChatChannelRepository;
import one.colla.common.ServiceTest;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

class ChatChannelServiceTest extends ServiceTest {

	@Autowired
	private TeamspaceService teamspaceService;

	@Autowired
	private ChatChannelRepository chatChannelRepository;

	@Autowired
	private UserChatChannelRepository userChatChannelRepository;

	@Autowired
	private ChatChannelService chatChannelService;

	User USER1;
	User USER2;
	CustomUserDetails USER1_DETAILS;
	Teamspace OS_TEAMSPACE;
	UserTeamspace USER1_OS_USERTEAMSPACE;
	UserTeamspace USER2_OS_USERTEAMSPACE;

	@BeforeEach
	void setUp() {
		USER1 = testFixtureBuilder.buildUser(USER1());
		USER2 = testFixtureBuilder.buildUser(USER2());
		USER1_DETAILS = createCustomUserDetailsByUser(USER1);
		OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
		USER2_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));
	}

	@Nested
	@DisplayName("채팅 채널 생성시")
	class CreateChatChannelTest {

		CreateChatChannelRequest request;
		CreateChatChannelResponse response;

		@Test
		@DisplayName("생성에 성공한다.")
		void createChatChannel_Success() {

			// given
			request = new CreateChatChannelRequest("새로운 채팅 채널");

			// when
			response = chatChannelService.createChatChannel(USER1_DETAILS, OS_TEAMSPACE.getId(), request);

			// then
			final Optional<ChatChannel> savedChatChannel = chatChannelRepository.findById(response.chatChannelId());

			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(savedChatChannel).hasValueSatisfying((chatChannel) -> {
					assertThat(response.chatChannelId()).isEqualTo(chatChannel.getId());
					assertThat(chatChannel.getUserChatChannels()).hasSize(2);
				});

			});
		}

		@Test
		@DisplayName("팀스페이스에 참여하고 있지 않은 사용자면 예외가 발생한다.")
		void createChatChannel_Fail() {
			// given
			User OTHER_USER = testFixtureBuilder.buildUser(RANDOMUSER());
			CustomUserDetails OTHER_USER_DETAILS = createCustomUserDetailsByUser(OTHER_USER);

			// when & then
			assertThatThrownBy(
				() -> chatChannelService.createChatChannel(OTHER_USER_DETAILS, OS_TEAMSPACE.getId(), request))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());

		}
	}

	@Nested
	@DisplayName("채팅 채널 조회시")
	class GetChatChannelTest {

		ChatChannel FRONTEND_CHAT_CHANNEL;
		ChatChannel BACKEND_CHAT_CHANNEL;
		ChatChannelMessage CHAT_MESSAGE1;

		@BeforeEach
		void setUp() {
			/* 채팅 채널 생성 */
			FRONTEND_CHAT_CHANNEL = testFixtureBuilder.buildChatChannel(FRONTEND_CHAT_CHANNEL(OS_TEAMSPACE));
			BACKEND_CHAT_CHANNEL = testFixtureBuilder.buildChatChannel(BACKEND_CHAT_CHANNEL(OS_TEAMSPACE));

			/* 팀스페이스에 채팅 채널 추가 */
			OS_TEAMSPACE.addChatChannel(FRONTEND_CHAT_CHANNEL);
			OS_TEAMSPACE.addChatChannel(BACKEND_CHAT_CHANNEL);

			/* 채팅 채널 유저 참가 */
			testFixtureBuilder.buildUserChatChannel(
				FRONTEND_CHAT_CHANNEL.participateAllTeamspaceUser(OS_TEAMSPACE.getUserTeamspaces()));
			testFixtureBuilder.buildUserChatChannel(
				BACKEND_CHAT_CHANNEL.participateAllTeamspaceUser(OS_TEAMSPACE.getUserTeamspaces()));

			/* 채팅 채널 메세지 생성 */
			CHAT_MESSAGE1 = testFixtureBuilder.buildChatChannelMessage(CHAT_MESSAGE1(USER1, FRONTEND_CHAT_CHANNEL,
				ChatType.TEXT));

			/* 채팅채널에 last 메세지 업데이트 */
			FRONTEND_CHAT_CHANNEL.updateLastChatMessage(CHAT_MESSAGE1.getId());
		}

		@Test
		@DisplayName("조회에 성공한다.")
		void getChatChannel_Success() {

			// when
			ChatChannelsResponse response = chatChannelService.getChatChannels(USER1_DETAILS, OS_TEAMSPACE.getId());

			//then

			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(response.chatChannels().size()).isEqualTo(2);
				softly.assertThat(response.chatChannels().get(0).name())
					.isEqualTo(FRONTEND_CHAT_CHANNEL.getChatChannelName().getValue());
				softly.assertThat(response.chatChannels().get(0).lastChatMessage()).isEqualTo(
					CHAT_MESSAGE1.getContent().getValue());
				softly.assertThat(response.chatChannels().get(1).name())
					.isEqualTo(BACKEND_CHAT_CHANNEL.getChatChannelName().getValue());
				softly.assertThat(response.chatChannels().get(1).lastChatMessage()).isEqualTo(
					null);

			});

		}

		@Test
		@DisplayName("팀스페이스 접근 권한이 없으면 예외가 발생한다.")
		void getChatChannel_Fail() {

			// given
			User OTHER_USER = testFixtureBuilder.buildUser(RANDOMUSER());
			CustomUserDetails OTHER_USER_DETAILS = createCustomUserDetailsByUser(OTHER_USER);

			// when & then
			assertThatThrownBy(() -> chatChannelService.getChatChannels(OTHER_USER_DETAILS, OS_TEAMSPACE.getId()))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());

		}

	}

	@Nested
	@DisplayName("채팅 채널 이름 수정시")
	class UpdateChatChannelNameTest {

		UpdateChatChannelNameRequest request;
		ChatChannel FRONTEND_CHAT_CHANNEL;
		String NEW_CHAT_CHANNEL_NAME = "새로운 채팅 채널 이름";

		@BeforeEach
		void setUp() {
			FRONTEND_CHAT_CHANNEL = testFixtureBuilder.buildChatChannel(FRONTEND_CHAT_CHANNEL(OS_TEAMSPACE));
			OS_TEAMSPACE.addChatChannel(FRONTEND_CHAT_CHANNEL);
		}

		@Test
		@DisplayName("수정에 성공한다.")
		void updateChatChannelName_Success() {
			// given
			request = new UpdateChatChannelNameRequest(FRONTEND_CHAT_CHANNEL.getId(), NEW_CHAT_CHANNEL_NAME);

			// when
			chatChannelService.updateChatChannelName(USER1_DETAILS, OS_TEAMSPACE.getId(), request);

			// then
			final Optional<ChatChannel> updatedChatChannel = chatChannelRepository.findById(
				FRONTEND_CHAT_CHANNEL.getId());

			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(updatedChatChannel).hasValueSatisfying(chatChannel -> {
					assertThat(chatChannel.getChatChannelName().getValue()).isEqualTo(NEW_CHAT_CHANNEL_NAME);
				});
			});
		}

		@Test
		@DisplayName("팀스페이스 접근 권한이 없으면 예외가 발생한다.")
		void updateChatChannelName_Fail_NoAccess() {
			// given
			User OTHER_USER = testFixtureBuilder.buildUser(RANDOMUSER());
			CustomUserDetails OTHER_USER_DETAILS = createCustomUserDetailsByUser(OTHER_USER);
			request = new UpdateChatChannelNameRequest(FRONTEND_CHAT_CHANNEL.getId(), NEW_CHAT_CHANNEL_NAME);

			// when & then
			assertThatThrownBy(
				() -> chatChannelService.updateChatChannelName(OTHER_USER_DETAILS, OS_TEAMSPACE.getId(), request))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());
		}

		@Test
		@DisplayName("존재하지 않는 채팅 채널이면 예외가 발생한다.")
		void updateChatChannelName_Fail_NotFound() {
			// given
			request = new UpdateChatChannelNameRequest(999L, NEW_CHAT_CHANNEL_NAME);

			// when & then
			assertThatThrownBy(
				() -> chatChannelService.updateChatChannelName(USER1_DETAILS, OS_TEAMSPACE.getId(), request))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.NOT_FOUND_CHAT_CHANNEL.getMessage());
		}
	}

}
