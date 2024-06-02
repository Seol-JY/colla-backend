package one.colla.chat.domain;

import static one.colla.common.fixtures.ChatChannelFixtures.*;
import static one.colla.common.fixtures.ChatChannelMessageFixtures.*;
import static one.colla.common.fixtures.TeamspaceFixtures.*;
import static one.colla.common.fixtures.UserFixtures.*;
import static one.colla.common.fixtures.UserTeamspaceFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import one.colla.common.RepositoryTest;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.TeamspaceRepository;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

class ChatChannelMessageRepositoryTest extends RepositoryTest {

	@Autowired
	private ChatChannelMessageRepository chatChannelMessageRepository;

	@Autowired
	private ChatChannelRepository chatChannelRepository;

	@Autowired
	private TeamspaceRepository teamspaceRepository;

	@Autowired
	private UserRepository userRepository;

	User USER1;
	User USER2;
	CustomUserDetails USER1_DETAILS;
	Teamspace OS_TEAMSPACE;
	UserTeamspace USER1_OS_USERTEAMSPACE;
	UserTeamspace USER2_OS_USERTEAMSPACE;
	ChatChannel FRONTEND_CHAT_CHANNEL;
	ChatChannel BACKEND_CHAT_CHANNEL;

	@BeforeEach
	void setUp() {
		USER1 = testFixtureBuilder.buildUser(USER1());
		USER2 = testFixtureBuilder.buildUser(USER2());
		USER1_DETAILS = createCustomUserDetailsByUser(USER1);

		/* 팀스페이스 생성 & 참가 */
		OS_TEAMSPACE = testFixtureBuilder.buildTeamspace(OS_TEAMSPACE());
		USER1_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(LEADER_USERTEAMSPACE(USER1, OS_TEAMSPACE));
		USER2_OS_USERTEAMSPACE = testFixtureBuilder.buildUserTeamspace(MEMBER_USERTEAMSPACE(USER2, OS_TEAMSPACE));

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
	}

	@Test
	@DisplayName("채팅 메시지를 생성 날짜 기준 내림차순으로 조회할 수 있다.")
	void findChatChannelMessageByChatChannelAndCriteria_Success() {
		// given
		List<ChatChannelMessage> messages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ChatChannelMessage message = chatChannelMessageRepository.save(
				RANDOM_CHAT_MESSAGE(USER1, OS_TEAMSPACE, FRONTEND_CHAT_CHANNEL));
			messages.add(message);
		}

		Pageable pageable = PageRequest.of(0, 5);

		// when
		List<ChatChannelMessage> findMessages = chatChannelMessageRepository.findChatChannelMessageByChatChannelAndCriteria(
			FRONTEND_CHAT_CHANNEL, null, pageable);

		// then
		assertThat(findMessages).hasSize(5);
		assertThat(findMessages.get(0).getCreatedAt()).isAfterOrEqualTo(
			findMessages.get(findMessages.size() - 1).getCreatedAt());
	}

	@Test
	@DisplayName("before 파라미터를 사용하여 채팅 메시지를 필터링할 수 있다.")
	void findChatChannelMessageByChatChannelAndCriteria_BeforeParameter() {
		// given
		List<ChatChannelMessage> messages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ChatChannelMessage message = chatChannelMessageRepository.save(
				RANDOM_CHAT_MESSAGE(USER1, OS_TEAMSPACE, FRONTEND_CHAT_CHANNEL));
			messages.add(message);
		}

		ChatChannelMessage beforeMessage = messages.get(4);

		Pageable pageable = PageRequest.of(0, 5);

		// when
		List<ChatChannelMessage> findMessages = chatChannelMessageRepository.findChatChannelMessageByChatChannelAndCriteria(
			FRONTEND_CHAT_CHANNEL, beforeMessage.getId(), pageable);

		// then
		assertThat(findMessages).hasSize(4);
		assertThat(findMessages.get(0).getCreatedAt()).isBefore(beforeMessage.getCreatedAt());
	}

}

