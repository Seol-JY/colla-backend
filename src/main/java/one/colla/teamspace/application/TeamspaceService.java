package one.colla.teamspace.application;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.util.RandomCodeGenerator;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.infra.redis.invite.InviteCode;
import one.colla.infra.redis.invite.InviteCodeService;
import one.colla.teamspace.application.dto.request.CreateTeamspaceRequest;
import one.colla.teamspace.application.dto.request.ParticipateRequest;
import one.colla.teamspace.application.dto.request.SendMailInviteCodeRequest;
import one.colla.teamspace.application.dto.response.CreateTeamspaceResponse;
import one.colla.teamspace.application.dto.response.InviteCodeResponse;
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
	private static final int INVITE_CODE_LENGTH = 10;
	private static final int VALID_HOURS = 72;
	private static final int SECONDS_PER_HOUR = 3_600;
	private static final int MAX_TEAMSPACE_USERS = 10;

	private final InviteCodeService inviteCodeService;
	private final TeamspaceRepository teamspaceRepository;
	private final UserTeamspaceRepository userTeamspaceRepository;
	private final UserRepository userRepository;
	private final RandomCodeGenerator randomCodeGenerator;

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

		final Optional<User> user = userDetails.map(details -> userRepository.findById(details.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER)));

		final Teamspace teamspace = teamspaceRepository.findById(teamspaceId).orElseThrow(
			() -> new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE)
		);

		boolean isParticipatedUser = user.map(u -> userTeamspaceRepository.existsByUserAndTeamspace(u, teamspace))
			.orElse(false);

		log.info("팀스페이스 정보조회 - 팀스페이스 Id: {}, 조회 사용자 Id: {}", teamspace.getId(), user.map(User::getId).orElse(null));
		return TeamspaceInfoResponse.of(isParticipatedUser, teamspace);
	}

	@Transactional
	public InviteCodeResponse getInviteCode(CustomUserDetails userDetails, Long teamspaceId) {
		InviteCode inviteCode = generateAndSaveInviteCodeByTeamspaceId(userDetails, teamspaceId);
		log.info("초대코드 생성(Copy) - 팀스페이스 Id: {}, 초대코드: {}", teamspaceId, inviteCode.getCode());
		return InviteCodeResponse.from(inviteCode);
	}

	@Transactional
	public void sendInviteCode(CustomUserDetails userDetails, Long teamspaceId, SendMailInviteCodeRequest request) {
		InviteCode inviteCode = generateAndSaveInviteCodeByTeamspaceId(userDetails, teamspaceId);
		// TODO: 실제 메일을 보내는 로직 작성 필요
		log.info("초대코드 생성(Mail) - 팀스페이스 Id: {}, 초대코드: {}", teamspaceId, inviteCode.getCode());
	}

	@Transactional
	public void participate(CustomUserDetails userDetails, Long teamspaceId, ParticipateRequest request) {
		final User user = userRepository.findById(userDetails.getUserId())
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));

		final Teamspace teamspace = teamspaceRepository.findById(teamspaceId).orElseThrow(
			() -> new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE)
		);

		if (!Objects.equals(teamspace.getId(), inviteCodeService.getTeamspaceIdByCode(request.inviteCode()))) {
			log.info("팀스페이스 참가 실패(초대코드-팀 불일치) - 팀스페이스 Id: {}, 사용자 Id: {}", teamspaceId, user.getId());
			throw new CommonException(ExceptionCode.INVALID_OR_EXPIRED_INVITATION_CODE);
		}
		if (teamspace.getUserTeamspaces().size() > MAX_TEAMSPACE_USERS) {
			log.info("팀스페이스 참가 실패(인원 초과) - 팀스페이스 Id: {}, 사용자 Id: {}", teamspaceId, user.getId());
			throw new CommonException(ExceptionCode.TEAMSPACE_FULL);
		}

		final UserTeamspace participatedUserTeamspace = user.participate(teamspace, TeamspaceRole.MEMBER);
		userTeamspaceRepository.save(participatedUserTeamspace);
		log.info("팀스페이스 참가 - 팀스페이스 Id: {}, 사용자 Id: {}", teamspaceId, user.getId());
	}

	public UserTeamspace getUserTeamspace(CustomUserDetails userDetails, Long teamspaceId) {
		return userTeamspaceRepository.findByUserIdAndTeamspaceId(userDetails.getUserId(),
			teamspaceId).orElseThrow(() -> new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE)
		);
	}

	private InviteCode generateAndSaveInviteCodeByTeamspaceId(CustomUserDetails userDetails, Long teamspaceId) {
		UserTeamspace userTeamspace = getUserTeamspace(userDetails, teamspaceId);

		int validSeconds = VALID_HOURS * SECONDS_PER_HOUR;

		String generatedCode = "";
		boolean exists = true;

		do {
			generatedCode = randomCodeGenerator.generateRandomString(INVITE_CODE_LENGTH);
			exists = inviteCodeService.existsByCode(generatedCode);
		} while (exists);

		return inviteCodeService.saveInviteCode(
			InviteCode.of(generatedCode, userTeamspace.getTeamspace().getId(), validSeconds));
	}
}

