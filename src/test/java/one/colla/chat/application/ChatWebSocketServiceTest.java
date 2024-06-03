package one.colla.chat.application;

import static one.colla.common.fixtures.ChatChannelFixtures.*;
import static one.colla.common.fixtures.ChatChannelMessageFixtures.*;
import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import one.colla.chat.application.dto.request.ChatCreateRequest;
import one.colla.chat.application.dto.response.ChatChannelMessageResponse;
import one.colla.chat.application.dto.response.ChatChannelStatusResponse;
import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatChannelMessageRepository;
import one.colla.chat.domain.ChatType;
import one.colla.chat.domain.UserChatChannel;
import one.colla.common.ServiceTest;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

class ChatWebSocketServiceTest extends ServiceTest {

	@Autowired
	private TeamspaceService teamspaceService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChatChannelMessageRepository chatChannelMessageRepository;

	@Autowired
	private ChatWebSocketService chatWebSocketService;

	User USER1;
	User USER2;
	CustomUserDetails USER1_DETAILS;
	Teamspace OS_TEAMSPACE;
	Teamspace DB_TEAMSPACE;
	UserTeamspace USER1_OS_USERTEAMSPACE;
	UserTeamspace USER2_OS_USERTEAMSPACE;
	ChatChannel FRONTEND_CHAT_CHANNEL;
	ChatChannel BACKEND_CHAT_CHANNEL;
	ChatChannel NO_EXIST_CHAT_CHANNEL;
	ChatChannelMessage CHAT_MESSAGE;
	List<UserChatChannel> USERS_PARTICIPATED_IN_FRONTEND_CHAT_CHANNEL;
	List<UserChatChannel> USERS_PARTICIPATED_IN_BACKEND_CHAT_CHANNEL;

	@BeforeEach
	void setUp() {
		USER1 = testFixtureBuilder.buildUser(USER1());
		USER2 = testFixtureBuilder.buildUser(USER2());
		USER1_DETAILS = createCustomUserDetailsByUser(USER1);

		OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		DB_TEAMSPACE = testFixtureBuilder.buildTeamspace(DATABASE_TEAMSPACE());
		USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
		USER2_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));

		FRONTEND_CHAT_CHANNEL = testFixtureBuilder.buildChatChannel(FRONTEND_CHAT_CHANNEL(OS_TEAMSPACE));
		BACKEND_CHAT_CHANNEL = testFixtureBuilder.buildChatChannel(BACKEND_CHAT_CHANNEL(OS_TEAMSPACE));
		NO_EXIST_CHAT_CHANNEL = testFixtureBuilder.buildChatChannel(NO_EXIST_CHAT_CHANNEL(DB_TEAMSPACE));

		OS_TEAMSPACE.addChatChannel(FRONTEND_CHAT_CHANNEL);
		OS_TEAMSPACE.addChatChannel(BACKEND_CHAT_CHANNEL);

		USERS_PARTICIPATED_IN_FRONTEND_CHAT_CHANNEL = testFixtureBuilder.buildUserChatChannel(
			FRONTEND_CHAT_CHANNEL.participateAllTeamspaceUser(OS_TEAMSPACE.getUserTeamspaces()));
		USERS_PARTICIPATED_IN_BACKEND_CHAT_CHANNEL = testFixtureBuilder.buildUserChatChannel(
			BACKEND_CHAT_CHANNEL.participateAllTeamspaceUser(OS_TEAMSPACE.getUserTeamspaces()));
	}

	@Nested
	@DisplayName("채팅 메시지 생성시")
	class ProcessMessageTest {

		ChatCreateRequest request = new ChatCreateRequest(ChatType.TEXT, "채팅 메시지 생성 요청", null, null);

		@Test
		@DisplayName("텍스트 메시지 생성에 성공한다.")
		void processTextMessage_Success() {
			// given
			ChatCreateRequest request = new ChatCreateRequest(ChatType.TEXT, "텍스트 메시지", null, null);

			// when
			ChatChannelMessageResponse response = chatWebSocketService.processMessage(
				request, USER1.getId(), OS_TEAMSPACE.getId(), FRONTEND_CHAT_CHANNEL.getId());

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.content()).isEqualTo(request.content());
				softly.assertThat(response.chatChannelId()).isEqualTo(FRONTEND_CHAT_CHANNEL.getId());
				softly.assertThat(response.author().id()).isEqualTo(USER1.getId());
				softly.assertThat(FRONTEND_CHAT_CHANNEL.getLastChatId()).isEqualTo(response.id());
			});

			Optional<ChatChannelMessage> savedMessage = chatChannelMessageRepository.findById(response.id());
			assertThat(savedMessage).isPresent();
		}

		@Test
		@DisplayName("이미지 메시지 생성에 성공한다.")
		void processImageMessage_Success() {
			// given
			final String FILE1_NAME = "image1.jpg";
			final String FILE2_NAME = "image2.jpg";

			final String FILE1_URL = "https://cdn.colla.so/image1.jpg";
			final String FILE2_URL = "https://cdn.colla.so/image2.jpg";

			List<ChatCreateRequest.FileDto> images = List.of(
				new ChatCreateRequest.FileDto(FILE1_NAME, FILE1_URL, 1024L),
				new ChatCreateRequest.FileDto(FILE2_NAME, FILE2_URL, 2048L)
			);
			ChatCreateRequest request = new ChatCreateRequest(ChatType.IMAGE, null, images, null);

			// when
			ChatChannelMessageResponse response = chatWebSocketService.processMessage(
				request, USER1.getId(), OS_TEAMSPACE.getId(), FRONTEND_CHAT_CHANNEL.getId());

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.chatChannelId()).isEqualTo(FRONTEND_CHAT_CHANNEL.getId());
				softly.assertThat(response.author().id()).isEqualTo(USER1.getId());
				softly.assertThat(response.attachments()).hasSize(2);
				softly.assertThat(response.attachments().get(0).filename()).isEqualTo(FILE1_NAME);
				softly.assertThat(response.attachments().get(1).filename()).isEqualTo(FILE2_NAME);
				softly.assertThat(FRONTEND_CHAT_CHANNEL.getLastChatId()).isEqualTo(response.id());
			});

			Optional<ChatChannelMessage> savedMessage = chatChannelMessageRepository.findById(response.id());
			assertThat(savedMessage).isPresent();
		}

		@Test
		@DisplayName("파일 메시지 생성에 성공한다.")
		void processFileMessage_Success() {
			// given
			final String ATTACHMENT1_NAME = "image1.jpg";
			final String ATTACHMENT2_NAME = "image2.jpg";

			final String ATTACHMENT1_URL = "https://cdn.colla.so/image1.jpg";
			final String ATTACHMENT2_URL = "https://cdn.colla.so/image2.jpg";

			List<ChatCreateRequest.FileDto> attachments = List.of(
				new ChatCreateRequest.FileDto(ATTACHMENT1_NAME, ATTACHMENT1_URL, 5120L),
				new ChatCreateRequest.FileDto(ATTACHMENT2_NAME, ATTACHMENT2_URL, 10240L)
			);
			ChatCreateRequest request = new ChatCreateRequest(ChatType.FILE, null, null, attachments);

			// when
			ChatChannelMessageResponse response = chatWebSocketService.processMessage(
				request, USER1.getId(), OS_TEAMSPACE.getId(), FRONTEND_CHAT_CHANNEL.getId());

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.chatChannelId()).isEqualTo(FRONTEND_CHAT_CHANNEL.getId());
				softly.assertThat(response.author().id()).isEqualTo(USER1.getId());
				softly.assertThat(response.attachments()).hasSize(2);
				softly.assertThat(response.attachments().get(0).filename()).isEqualTo(ATTACHMENT1_NAME);
				softly.assertThat(response.attachments().get(1).filename()).isEqualTo(ATTACHMENT2_NAME);
				softly.assertThat(FRONTEND_CHAT_CHANNEL.getLastChatId()).isEqualTo(response.id());
			});

			Optional<ChatChannelMessage> savedMessage = chatChannelMessageRepository.findById(response.id());
			assertThat(savedMessage).isPresent();
		}

		@Test
		@DisplayName("존재하지 않는 유저로 메시지 생성 시도시 예외가 발생한다.")
		void processMessage_Fail_UserNotFound() {

			// when & then
			assertThatThrownBy(() -> chatWebSocketService.processMessage(
				request, 999L, OS_TEAMSPACE.getId(), FRONTEND_CHAT_CHANNEL.getId()))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		@DisplayName("팀스페이스에 참여하지 않은 유저가 메시지 생성 시도시 예외가 발생한다.")
		void processMessage_Fail_UserNotInTeamspace() {
			// given
			User OTHER_USER = testFixtureBuilder.buildUser(RANDOMUSER());

			// when & then
			assertThatThrownBy(
				() -> chatWebSocketService.processMessage(
					request, OTHER_USER.getId(), OS_TEAMSPACE.getId(), FRONTEND_CHAT_CHANNEL.getId()))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());
		}

		@Test
		@DisplayName("존재하지 않는 채널로 메시지 생성 시도시 예외가 발생한다.")
		void processMessage_Fail_ChannelNotFound() {

			// when & then
			assertThatThrownBy(
				() -> chatWebSocketService.processMessage(request, USER1.getId(), OS_TEAMSPACE.getId(), 999L))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.NOT_FOUND_CHAT_CHANNEL.getMessage());
		}
	}

	@Nested
	@DisplayName("채팅 채널 상태 조회시")
	class GetChatChannelsStatusTest {

		@Test
		@DisplayName("채팅 채널 상태 조회에 성공한다.")
		void getChatChannelsStatus_Success() {
			// given
			CHAT_MESSAGE = testFixtureBuilder.buildChatChannelMessage(
				CHAT_MESSAGE1(USER1, OS_TEAMSPACE, FRONTEND_CHAT_CHANNEL));

			FRONTEND_CHAT_CHANNEL.updateLastChatMessage(CHAT_MESSAGE.getId());

			// when
			ChatChannelStatusResponse response = chatWebSocketService.getChatChannelsStatus(USER1.getId(),
				OS_TEAMSPACE.getId());

			// then
			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(response).isNotNull();
				softly.assertThat(response.chatChannelsResponse()).hasSize(2);
				softly.assertThat(response.chatChannelsResponse().get(0).id()).isEqualTo(FRONTEND_CHAT_CHANNEL.getId());
				softly.assertThat(response.chatChannelsResponse().get(0).lastChatMessage())
					.isEqualTo(CHAT_MESSAGE.getContent().getValue());
				softly.assertThat(response.chatChannelsResponse().get(1).id()).isEqualTo(BACKEND_CHAT_CHANNEL.getId());
				softly.assertThat(response.chatChannelsResponse().get(1).lastChatMessage()).isEqualTo(null);

			});
		}

		@Test
		@DisplayName("존재하지 않는 유저로 채널 상태 조회 시도시 예외가 발생한다.")
		void getChatChannelsStatus_Fail_UserNotFound() {
			// when & then
			assertThatThrownBy(() -> chatWebSocketService.getChatChannelsStatus(999L, OS_TEAMSPACE.getId()))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.NOT_FOUND_USER.getMessage());
		}

		@Test
		@DisplayName("팀스페이스에 참여하지 않은 유저가 채널 상태 조회 시도시 예외가 발생한다.")
		void getChatChannelsStatus_Fail_UserNotInTeamspace() {
			// given
			User OTHER_USER = testFixtureBuilder.buildUser(RANDOMUSER());

			// when & then
			assertThatThrownBy(
				() -> chatWebSocketService.getChatChannelsStatus(OTHER_USER.getId(), OS_TEAMSPACE.getId()))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());
		}
	}

	@Nested
	@DisplayName("메시지 읽음 처리시")
	class MarkMessageAsReadTest {

		@Test
		@DisplayName("메시지를 읽음으로 표시한다")
		void markMessageAsRead_Success() {
			// given
			CHAT_MESSAGE = testFixtureBuilder.buildChatChannelMessage(
				CHAT_MESSAGE1(USER1, OS_TEAMSPACE, FRONTEND_CHAT_CHANNEL));

			FRONTEND_CHAT_CHANNEL.updateLastChatMessage(CHAT_MESSAGE.getId());

			// when
			chatWebSocketService.markMessageAsRead(USER1.getId(), OS_TEAMSPACE.getId(), FRONTEND_CHAT_CHANNEL.getId(),
				CHAT_MESSAGE.getId());

			// then
			assertThat(USERS_PARTICIPATED_IN_FRONTEND_CHAT_CHANNEL.get(0).getLastReadMessageId()).isEqualTo(
				CHAT_MESSAGE.getId());
		}

		@Test
		@DisplayName("팀스페이스 권한이 없는 경우 예외가 발생한다.")
		void markMessageAsRead_UserNotFound() {
			// given
			User OTHER_USER = testFixtureBuilder.buildUser(RANDOMUSER());
			CHAT_MESSAGE = testFixtureBuilder.buildChatChannelMessage(
				CHAT_MESSAGE1(USER1, OS_TEAMSPACE, FRONTEND_CHAT_CHANNEL));

			FRONTEND_CHAT_CHANNEL.updateLastChatMessage(CHAT_MESSAGE.getId());

			// when & then
			assertThatThrownBy(
				() -> chatWebSocketService.markMessageAsRead(OTHER_USER.getId(), OS_TEAMSPACE.getId(),
					FRONTEND_CHAT_CHANNEL.getId(), CHAT_MESSAGE.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());
		}

		@Test
		@DisplayName("채널이 존재하지 않을 때 예외가 발생한다.")
		void markMessageAsRead_ChannelNotFound() {
			// given
			CHAT_MESSAGE = testFixtureBuilder.buildChatChannelMessage(
				CHAT_MESSAGE1(USER1, OS_TEAMSPACE, FRONTEND_CHAT_CHANNEL));

			FRONTEND_CHAT_CHANNEL.updateLastChatMessage(CHAT_MESSAGE.getId());

			// when & then
			assertThatThrownBy(
				() -> chatWebSocketService.markMessageAsRead(USER1.getId(), OS_TEAMSPACE.getId(),
					NO_EXIST_CHAT_CHANNEL.getId(), CHAT_MESSAGE.getId()))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.NOT_FOUND_CHAT_CHANNEL.getMessage());
		}
	}

}
