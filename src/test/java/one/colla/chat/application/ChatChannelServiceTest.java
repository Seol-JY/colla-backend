package one.colla.chat.application;

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
import one.colla.chat.application.dto.response.CreateChatChannelResponse;
import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelRepository;
import one.colla.chat.domain.UserChatChannelRepository;
import one.colla.common.ServiceTest;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
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

	@Nested
	@DisplayName("채팅 채널 생성시")
	class CreateChatChannelTest {

		User USER1;
		User USER2;
		CustomUserDetails USER1_DETAILS;
		Teamspace OS_TEAMSPACE;
		CreateChatChannelRequest request;
		CreateChatChannelResponse response;

		@BeforeEach
		void setUp() {
			USER1 = testFixtureBuilder.buildUser(USER1());
			USER2 = testFixtureBuilder.buildUser(USER2());
			USER1_DETAILS = createCustomUserDetailsByUser(USER1);
			OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
			request = new CreateChatChannelRequest("새로운 채팅 채널");

		}

		@Test
		@DisplayName("생성에 성공한다.")
		void createChatChannel_Success() {
			// given
			testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
			testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));

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

			// when & then
			assertThatThrownBy(() -> chatChannelService.createChatChannel(USER1_DETAILS, OS_TEAMSPACE.getId(), request))
				.isExactlyInstanceOf(CommonException.class)
				.hasMessageContaining(ExceptionCode.FORBIDDEN_TEAMSPACE.getMessage());

		}
	}

}
