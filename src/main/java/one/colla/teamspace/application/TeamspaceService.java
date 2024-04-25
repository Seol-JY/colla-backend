package one.colla.teamspace.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.infra.redis.invite.InviteCodeService;
import one.colla.teamspace.application.dto.request.CreateTeamspaceRequest;
import one.colla.teamspace.application.dto.response.CreateTeamspaceResponse;
import one.colla.teamspace.application.dto.response.TeamspaceInfoResponse;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.TeamspaceRepository;
import one.colla.teamspace.domain.TeamspaceRole;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.teamspace.domain.UserTeamspaceRepository;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamspaceService {
	private final InviteCodeService inviteCodeService;
	private final TeamspaceRepository teamspaceRepository;
	private final UserTeamspaceRepository userTeamspaceRepository;
	private final UserRepository userRepository;

	@Transactional
	public CreateTeamspaceResponse create(final CustomUserDetails userDetails, final CreateTeamspaceRequest request) {
		final User user = userRepository.findById(userDetails.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

		final Teamspace createdTeamspace = teamspaceRepository.save(Teamspace.from(request.teamspaceName()));
		final UserTeamspace participatedUserTeamspace = user.participate(createdTeamspace, TeamspaceRole.LEADER);

		userTeamspaceRepository.save(participatedUserTeamspace);

		log.info("팀스페이스 생성 - 팀스페이스 Id: {}, 생성한 사용자 Id: {}", createdTeamspace.getId(), user.getId());
		return CreateTeamspaceResponse.from(createdTeamspace);
	}

	@Transactional(readOnly = true)
	public TeamspaceInfoResponse readInfoByCode(Optional<CustomUserDetails> userDetails, String inviteCode) {
		Long teamspaceId = inviteCodeService.getTeamspaceIdByCode(inviteCode);

		Optional<User> user = userDetails.map(details -> userRepository.findById(details.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER)));

		Teamspace teamspace = teamspaceRepository.findById(teamspaceId).orElseThrow(
			() -> new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE)
		);

		boolean isParticipatedUser = user.map(u -> userTeamspaceRepository.existsByUserAndTeamspace(u, teamspace))
			.orElse(false);

		log.info("팀스페이스 정보조회 - 팀스페이스 Id: {}, 조회 사용자 Id: {}", teamspace.getId(), user.map(User::getId).orElse(null));
		return TeamspaceInfoResponse.of(isParticipatedUser, teamspace);
	}
}

