package one.colla.chat.application;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.chat.application.dto.request.CreateChatChannelRequest;
import one.colla.chat.application.dto.request.UpdateChatChannelNameRequest;
import one.colla.chat.application.dto.response.ChatChannelInfoDto;
import one.colla.chat.application.dto.response.ChatChannelMessageAttachmentDto;
import one.colla.chat.application.dto.response.ChatChannelMessageAuthorDto;
import one.colla.chat.application.dto.response.ChatChannelMessageInfoDto;
import one.colla.chat.application.dto.response.ChatChannelMessagesResponse;
import one.colla.chat.application.dto.response.ChatChannelsResponse;
import one.colla.chat.application.dto.response.CreateChatChannelResponse;
import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.ChatChannelMessageRepository;
import one.colla.chat.domain.ChatChannelRepository;
import one.colla.chat.domain.UserChatChannel;
import one.colla.chat.domain.UserChatChannelRepository;
import one.colla.chat.domain.vo.ChatChannelName;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatChannelService {

	private static final int PAGE_REQUEST_ZERO_OFFSET = 0;

	private final TeamspaceService teamspaceService;
	private final ChatChannelRepository chatChannelRepository;
	private final UserChatChannelRepository userChatChannelRepository;
	private final ChatChannelMessageRepository chatChannelMessageRepository;

	@Transactional
	public CreateChatChannelResponse createChatChannel(CustomUserDetails userDetails, Long teamspaceId,
		CreateChatChannelRequest request) {

		final Teamspace teamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId).getTeamspace();
		final ChatChannel createdChatChannel = chatChannelRepository.save(
			ChatChannel.of(teamspace, request.chatChannelName()));
		final List<UserChatChannel> participants = createdChatChannel.participateAllTeamspaceUser(
			teamspace.getUserTeamspaces());
		userChatChannelRepository.saveAll(participants);

		teamspace.addChatChannel(createdChatChannel);

		log.info("채팅 채널 생성 - 팀스페이스 Id: {}, 생성한 사용자 Id: {}, 생성한 채팅 채널 Id: {}",
			teamspaceId, userDetails.getUserId(), createdChatChannel.getId());
		return CreateChatChannelResponse.from(createdChatChannel);
	}

	@Transactional(readOnly = true)
	public ChatChannelsResponse getChatChannels(CustomUserDetails userDetails, Long teamspaceId) {

		final Teamspace teamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId).getTeamspace();

		List<ChatChannelInfoDto> chatChannels = teamspace.getChatChannels().stream()
			.map(this::createChatChannelInfoDto)
			.toList();
		log.info("채팅 채널 목록 조회 - 팀스페이스 Id: {}, 조회한 사용자 Id: {}", teamspaceId, userDetails.getUserId());
		return ChatChannelsResponse.from(chatChannels);
	}

	private ChatChannelInfoDto createChatChannelInfoDto(ChatChannel chatChannel) {
		Long lastChatId = chatChannel.getLastChatId();
		if (lastChatId == null) {
			return ChatChannelInfoDto.of(chatChannel, null, null);
		}

		return chatChannelMessageRepository.findById(lastChatId)
			.map(msg -> ChatChannelInfoDto.of(chatChannel, msg.getContent().getValue(), msg.getCreatedAt()))
			.orElseGet(() -> ChatChannelInfoDto.of(chatChannel, null, null));
	}

	@Transactional
	public void updateChatChannelName(CustomUserDetails userDetails, Long teamspaceId,
		UpdateChatChannelNameRequest request) {

		final Teamspace teamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId).getTeamspace();

		processChatChannelNameUpdate(request, teamspace);

		log.info("채팅 채널 이름 수정 - 팀스페이스 Id: {}, 수정한 사용자 Id: {}, 수정한 채팅 채널 Id: {}",
			teamspaceId, userDetails.getUserId(), request.chatChannelId());
	}

	private void processChatChannelNameUpdate(UpdateChatChannelNameRequest request, Teamspace teamspace) {
		ChatChannel chatChannel = teamspace.getChatChannels().stream()
			.filter(cc -> cc.getId().equals(request.chatChannelId()))
			.findFirst()
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_CHAT_CHANNEL));
		ChatChannelName chatChannelName = ChatChannelName.from(request.chatChannelName());
		chatChannel.updateChatChannelName(chatChannelName);
	}

	@Transactional(readOnly = true)
	public ChatChannelMessagesResponse getChatChanelMessages(CustomUserDetails userDetails, Long teamspaceId,
		Long chatChannelId, Long beforeChatMessageId, int limit) {

		final Teamspace teamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId).getTeamspace();
		final ChatChannel chatChannel = getChatChannel(teamspace, chatChannelId);

		validateBeforeChatMessageId(beforeChatMessageId, chatChannel);

		List<ChatChannelMessage> messages = fetchChatChannelMessages(chatChannel, beforeChatMessageId, limit);
		List<ChatChannelMessageInfoDto> chatChannelMessageInfoDtos = convertToInfoDtos(messages);

		log.info("채팅 채널 메세지 조회 - 팀스페이스 Id: {}, 조회한 사용자 Id: {}, 채팅 채널 Id: {} ",
			teamspaceId, userDetails.getUserId(), chatChannel.getId());
		return ChatChannelMessagesResponse.from(chatChannelMessageInfoDtos);
	}

	private ChatChannel getChatChannel(Teamspace teamspace, Long chatChannelId) {
		return teamspace.getChatChannels()
			.stream()
			.filter(ch -> ch.getId().equals(chatChannelId))
			.findFirst()
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_CHAT_CHANNEL));
	}

	private void validateBeforeChatMessageId(Long beforeChatMessageId, ChatChannel chatChannel) {
		if (beforeChatMessageId != null && chatChannelMessageRepository.findByIdAndChatChannel(
			beforeChatMessageId, chatChannel).isEmpty()) {
			throw new CommonException(ExceptionCode.NOT_FOUND_CHAT_CHANNEL_MESSAGE);
		}
	}

	private List<ChatChannelMessage> fetchChatChannelMessages(ChatChannel chatChannel, Long beforeChatMessageId,
		int limit) {
		PageRequest pageRequest = PageRequest.of(PAGE_REQUEST_ZERO_OFFSET, limit);
		return chatChannelMessageRepository.findChatChannelMessageByChatChannelAndCriteria(
			chatChannel, beforeChatMessageId, pageRequest);
	}

	private List<ChatChannelMessageInfoDto> convertToInfoDtos(List<ChatChannelMessage> messages) {
		return messages.stream()
			.map(msg -> {
				ChatChannelMessageAuthorDto author = ChatChannelMessageAuthorDto.from(msg.getUser());
				List<ChatChannelMessageAttachmentDto> attachments = getMessageAttachmentDtos(msg);
				return ChatChannelMessageInfoDto.of(msg, author, attachments);
			}).toList();
	}

	private static List<ChatChannelMessageAttachmentDto> getMessageAttachmentDtos(ChatChannelMessage msg) {
		return msg.getChatChannelMessageAttachments()
			.stream()
			.map(ChatChannelMessageAttachmentDto::from)
			.toList();
	}
}
