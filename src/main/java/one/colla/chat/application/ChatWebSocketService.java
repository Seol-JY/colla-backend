package one.colla.chat.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.chat.application.dto.request.ChatCreateRequest;
import one.colla.chat.application.dto.response.ChatChannelMessageAttachmentDto;
import one.colla.chat.application.dto.response.ChatChannelMessageAuthorDto;
import one.colla.chat.application.dto.response.ChatChannelMessageResponse;
import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatChannelMessageRepository;
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

	// TODO: 소켓 통신이지만 RDB에 너무 많은 접근이 있음. 추후 성능 개선 필요
	@Transactional
	public ChatChannelMessageResponse processMessage(
		ChatCreateRequest request, Long userId, Long teamspaceId, Long chatChannelId) {

		log.info(request.toString());

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));
		CustomUserDetails customUserDetails = CustomUserDetails.from(user);

		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(customUserDetails, teamspaceId);

		ChatChannel chatChannel = findChatChannel(userTeamspace, chatChannelId);

		ChatChannelMessage chatChannelMessage = ChatChannelMessage.of(
			user, userTeamspace.getTeamspace(), chatChannel, request);

		chatChannelMessageRepository.save(chatChannelMessage);

		log.info("채팅 메시지 생성 & 저장  - 사용자 Id: {}, 팀스페이스 Id: {}, 채널 Id: {}", userId, teamspaceId, chatChannelId);

		chatChannel.updateLastChatMessage(chatChannelMessage.getId());

		ChatChannelMessageAuthorDto chatChannelMessageAuthorDto = ChatChannelMessageAuthorDto.from(user);
		List<ChatChannelMessageAttachmentDto> chatChannelMessageAttachmentDtos =
			chatChannelMessage.getChatChannelMessageAttachments().stream()
				.map(ChatChannelMessageAttachmentDto::from).toList();

		log.info("채팅 메시지 응답 생성 - 사용자 Id: {}, 팀스페이스 Id: {}, 채널 Id: {}", userId, teamspaceId, chatChannelId);

		return ChatChannelMessageResponse.of(
			chatChannelMessage, chatChannelMessageAuthorDto, chatChannelMessageAttachmentDtos);
	}

	@Transactional(readOnly = true)
	public void getChatChannelStatuses(Long userId, Long teamspaceId, Long chatChannelId) {

	}

	private ChatChannel findChatChannel(UserTeamspace userTeamspace, Long chatChannelId) {
		return userTeamspace.getTeamspace().getChatChannels().stream()
			.filter(cc -> cc.getId().equals(chatChannelId))
			.findFirst()
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_CHAT_CHANNEL));
	}
}
