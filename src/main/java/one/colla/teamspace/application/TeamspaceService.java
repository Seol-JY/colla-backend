package one.colla.teamspace.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.dto.request.CreateTeamspaceRequest;
import one.colla.teamspace.application.dto.response.CreateTeamspaceResponse;
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
	private final TeamspaceRepository teamspaceRepository;
	private final UserTeamspaceRepository userTeamspaceRepository;
	private final UserRepository userRepository;

	@Transactional
	public CreateTeamspaceResponse create(CustomUserDetails userDetails, CreateTeamspaceRequest request) {
		final User user = userRepository.findById(userDetails.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

		final Teamspace createdTeamspace = teamspaceRepository.save(Teamspace.from(request.teamspaceName()));
		final UserTeamspace participatedUserTeamspace = user.participate(createdTeamspace, TeamspaceRole.LEADER);

		userTeamspaceRepository.save(participatedUserTeamspace);

		log.info("팀스페이스 생성 - 팀스페이스 Id: {}, 생성한 사용자 Id: {}", createdTeamspace.getId(), user.getId());
		return CreateTeamspaceResponse.from(createdTeamspace);
	}
}
