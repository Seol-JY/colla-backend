package one.colla.user.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.infra.redis.lastseen.LastSeenTeamspace;
import one.colla.infra.redis.lastseen.LastSeenTeamspaceService;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.application.dto.request.LastSeenUpdateRequest;
import one.colla.user.application.dto.response.ParticipatedTeamspaceDto;
import one.colla.user.application.dto.response.ProfileDto;
import one.colla.user.application.dto.response.UserStatusResponse;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final LastSeenTeamspaceService lastSeenTeamspaceService;
	private final TeamspaceService teamspaceService;

	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public UserStatusResponse getUserStatus(CustomUserDetails userDetails) {
		final User user = userRepository.findById(userDetails.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

		Long lastSeenTeamspaceId = lastSeenTeamspaceService.findByUserId(user.getId())
			.map(LastSeenTeamspace::getTeamspaceId)
			.orElse(null);

		ProfileDto profile = ProfileDto.of(user.getId(), user, lastSeenTeamspaceId);

		List<ParticipatedTeamspaceDto> participatedTeamspaces = user.getUserTeamspaces().stream()
			.map(ut -> ParticipatedTeamspaceDto.of(
				ut.getTeamspace().getId(),
				ut,
				getNumOfTeamspaceParticipants(ut))
			)
			.toList();

		return UserStatusResponse.of(profile, participatedTeamspaces);
	}

	public void updateLastSeenTeamspace(CustomUserDetails userDetails, LastSeenUpdateRequest request) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, request.teamspaceId());

		lastSeenTeamspaceService.updateLastSeenTeamspace(
			userTeamspace.getUser().getId(),
			userTeamspace.getTeamspace().getId()
		);
	}

	private int getNumOfTeamspaceParticipants(UserTeamspace userTeamspace) {
		return userTeamspace.getTeamspace()
			.getUserTeamspaces()
			.size();
	}

}
