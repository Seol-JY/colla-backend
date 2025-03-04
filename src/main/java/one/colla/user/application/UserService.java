package one.colla.user.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.chat.domain.ChatChannelMessageRepository;
import one.colla.chat.domain.UserChatChannelRepository;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.infra.redis.lastseen.LastSeenTeamspace;
import one.colla.infra.redis.lastseen.LastSeenTeamspaceService;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.application.dto.request.LastSeenUpdateRequest;
import one.colla.user.application.dto.request.UpdateUserSettingRequest;
import one.colla.user.application.dto.response.ParticipatedTeamspaceDto;
import one.colla.user.application.dto.response.ProfileDto;
import one.colla.user.application.dto.response.UserStatusResponse;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;
import one.colla.user.domain.vo.UserProfileImageUrl;
import one.colla.user.domain.vo.Username;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final LastSeenTeamspaceService lastSeenTeamspaceService;
	private final TeamspaceService teamspaceService;
	private final UserChatChannelRepository userChatChannelRepository;
	private final ChatChannelMessageRepository chatChannelMessageRepository;

	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public UserStatusResponse getUserStatus(CustomUserDetails userDetails) {
		final User user = userRepository.findByIdWithTeamspaces(userDetails.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

		Long lastSeenTeamspaceId = lastSeenTeamspaceService.findByUserId(user.getId())
			.map(LastSeenTeamspace::getTeamspaceId)
			.orElse(null);
		ProfileDto profile = ProfileDto.of(user.getId(), user, lastSeenTeamspaceId);

		List<Long> teamspaceIds = user.getUserTeamspaces().stream()
			.map(ut -> ut.getTeamspace().getId())
			.toList();
		Map<Long, Long> participantCountMap = teamspaceService.countParticipantsByTeamspaceIds(teamspaceIds);
		List<ParticipatedTeamspaceDto> participatedTeamspaces = user.getUserTeamspaces().stream()
			.map(ut -> {
				Long teamspaceId = ut.getTeamspace().getId();
				return ParticipatedTeamspaceDto.of(
					teamspaceId,
					ut,
					participantCountMap.getOrDefault(teamspaceId, 0L).intValue(),
					1 // 읽지 않은 메세지 임시 처리 (향후 최적화 가능)
				);
			})
			.toList();

		log.info("사용자 관련 정보 조회 - 사용자 Id: {}", userDetails.getUserId());
		return UserStatusResponse.of(profile, participatedTeamspaces);
	}

	// TODO: API  분리 필요
	// private int calculateUnreadMessageCount(Long userId, Teamspace teamspace) {
	// 	return teamspace.getChatChannels().stream()
	// 		.mapToInt(chatChannel -> userChatChannelRepository.findByUserIdAndChatChannelId(userId, chatChannel.getId())
	// 			.map(userChatChannel -> {
	// 				Long lastReadMessageId = userChatChannel.getLastReadMessageId();
	// 				if (lastReadMessageId == null) {
	// 					return chatChannelMessageRepository.countByChatChannel(chatChannel);
	// 				} else {
	// 					return chatChannelMessageRepository.countByChatChannelAndIdGreaterThan(chatChannel,
	// 						lastReadMessageId);
	// 				}
	// 			})
	// 			.orElse(0))
	// 		.sum();
	// }

	@Transactional
	public void updateLastSeenTeamspace(CustomUserDetails userDetails, LastSeenUpdateRequest request) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, request.teamspaceId());

		lastSeenTeamspaceService.updateLastSeenTeamspace(
			userTeamspace.getUser().getId(),
			userTeamspace.getTeamspace().getId()
		);
		log.info("마지막 접근 팀스페이스 업데이트 - 팀스페이스 Id: {}, 사용자 Id: {}", request.teamspaceId(), userDetails.getUserId());
	}

	@Transactional(readOnly = true)
	public boolean hasTeam(Long userId) {
		final User user = userRepository.findById(userId)
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

		return !user.getUserTeamspaces().isEmpty();
	}

	@Transactional
	public void updateSettings(CustomUserDetails userDetails, UpdateUserSettingRequest request) {
		final User user = userRepository.findById(userDetails.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

		if (request.profileImageUrl() != null) {
			UserProfileImageUrl userProfileImageUrl = UserProfileImageUrl.from(request.profileImageUrl());
			user.changeProfileImageUrl(userProfileImageUrl);
		}

		if (request.emailSubscription() != null) {
			log.debug(request.emailSubscription().toString());
			user.changeEmailSubscription(request.emailSubscription());
		}

		if (request.commentNotification() != null) {
			user.changeCommentNotification(request.commentNotification());
		}

		if (request.username() != null) {
			Username username = Username.from(request.username());
			user.changeUsername(username);
		}

		log.info("사용자 설정 업데이트 - 사용자 Id: {}", userDetails.getUserId());
	}

	@Transactional
	public void deleteProfileImageUrl(CustomUserDetails userDetails) {
		final User user = userRepository.findById(userDetails.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

		user.deleteProfileImageUrl();
		log.info("사용자 프로필 사진 삭제 완료 - 사용자 Id: {}", userDetails.getUserId());
	}

	private int getNumOfTeamspaceParticipants(UserTeamspace userTeamspace) {
		return userTeamspace.getTeamspace()
			.getUserTeamspaces()
			.size();
	}
}
