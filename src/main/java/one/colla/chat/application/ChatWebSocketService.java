package one.colla.chat.application;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.chat.application.dto.request.ChatCreateRequest;
import one.colla.chat.application.dto.response.ChatChannelInfoDto;
import one.colla.chat.application.dto.response.ChatChannelMessageAttachmentDto;
import one.colla.chat.application.dto.response.ChatChannelMessageAuthorDto;
import one.colla.chat.application.dto.response.ChatChannelMessageResponse;
import one.colla.chat.application.dto.response.ChatChannelStatusResponse;
import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatChannelMessageRepository;
import one.colla.chat.domain.UserChatChannel;
import one.colla.chat.domain.UserChatChannelRepository;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWebSocketService {

	private final TeamspaceService teamspaceService;
	private final UserRepository userRepository;
	private final ChatChannelMessageRepository chatChannelMessageRepository;
	private final ChatChannelService chatChannelService;
	private final UserChatChannelRepository userChatChannelRepository;

	@Transactional
	public ChatChannelMessageResponse processMessage(
		ChatCreateRequest request, Long userId, Long teamspaceId, Long chatChannelId) {

		User user = findUserById(userId);
		CustomUserDetails customUserDetails = CustomUserDetails.from(user);

		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(customUserDetails, teamspaceId);
		ChatChannel chatChannel = findChatChannel(userTeamspace, chatChannelId);

		ChatChannelMessage chatChannelMessage = createAndSaveChatMessage(request, user, userTeamspace, chatChannel);

		log.info("채팅 메시지 생성 & 저장 - 사용자 Id: {}, 팀스페이스 Id: {}, 채널 Id: {}", userId, teamspaceId, chatChannelId);

		chatChannel.updateLastChatMessage(chatChannelMessage.getId());

		ChatChannelMessageResponse response = createChatMessageResponse(user, chatChannelMessage);

		log.info("채팅 메시지 응답 생성 - 사용자 Id: {}, 팀스페이스 Id: {}, 채널 Id: {}", userId, teamspaceId, chatChannelId);

		return response;
	}

	@Transactional(readOnly = true)
	public ChatChannelStatusResponse getChatChannelsStatus(Long userId, Long teamspaceId) {
		User user = findUserById(userId);
		CustomUserDetails customUserDetails = CustomUserDetails.from(user);

		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(customUserDetails, teamspaceId);

		List<ChatChannelInfoDto> chatChannelInfoDtos = userTeamspace.getTeamspace().getChatChannels().stream()
			.map(ch -> chatChannelService.createChatChannelInfoDto(ch, userId))
			.sorted(Comparator.comparing(ChatChannelInfoDto::lastChatCreatedAt,
				Comparator.nullsLast(Comparator.reverseOrder())))
			.toList();

		return ChatChannelStatusResponse.from(chatChannelInfoDtos);
	}

	@Transactional
	public void markMessageAsRead(Long userId, Long teamspaceId, Long chatChannelId, Long messageId) {
		User user = findUserById(userId);
		CustomUserDetails customUserDetails = CustomUserDetails.from(user);

		validateParticipationInTeamspace(teamspaceId, customUserDetails);

		UserChatChannel userChatChannel = userChatChannelRepository.findByUserIdAndChatChannelId(userId, chatChannelId)
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_CHAT_CHANNEL));

		if (userChatChannel.getLastReadMessageId() == null || userChatChannel.getLastReadMessageId() < messageId) {
			userChatChannel.updateLastReadMessageId(messageId);
			userChatChannelRepository.save(userChatChannel);
		}
	}

	private void validateParticipationInTeamspace(Long teamspaceId, CustomUserDetails customUserDetails) {
		teamspaceService.getUserTeamspace(customUserDetails, teamspaceId);
	}

	private ChatChannelMessage createAndSaveChatMessage(ChatCreateRequest request, User user,
		UserTeamspace userTeamspace, ChatChannel chatChannel) {
		ChatChannelMessage chatChannelMessage = ChatChannelMessage.of(
			user, userTeamspace.getTeamspace(), chatChannel, request);
		chatChannelMessageRepository.save(chatChannelMessage);
		return chatChannelMessage;
	}

	private ChatChannelMessageResponse createChatMessageResponse(User user, ChatChannelMessage chatChannelMessage) {
		ChatChannelMessageAuthorDto chatChannelMessageAuthorDto = ChatChannelMessageAuthorDto.from(user);
		List<ChatChannelMessageAttachmentDto> chatChannelMessageAttachmentDtos =
			chatChannelMessage.getChatChannelMessageAttachments().stream()
				.map(ChatChannelMessageAttachmentDto::from)
				.toList();
		return ChatChannelMessageResponse.of(chatChannelMessage, chatChannelMessageAuthorDto,
			chatChannelMessageAttachmentDtos);
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));
	}

	private ChatChannel findChatChannel(UserTeamspace userTeamspace, Long chatChannelId) {
		return userTeamspace.getTeamspace().getChatChannels().stream()
			.filter(cc -> cc.getId().equals(chatChannelId))
			.findFirst()
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_CHAT_CHANNEL));
	}
}
