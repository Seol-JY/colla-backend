package one.colla.teamspace.application;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.util.RandomCodeGenerator;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.infra.mail.events.InviteCodeSendMailEvent;
import one.colla.infra.redis.invite.InviteCode;
import one.colla.infra.redis.invite.InviteCodeService;
import one.colla.teamspace.application.dto.request.CreateTeamspaceRequest;
import one.colla.teamspace.application.dto.request.ParticipateRequest;
import one.colla.teamspace.application.dto.request.SendMailInviteCodeRequest;
import one.colla.teamspace.application.dto.response.CreateTeamspaceResponse;
import one.colla.teamspace.application.dto.response.InviteCodeResponse;
import one.colla.teamspace.application.dto.response.ParticipantDto;
import one.colla.teamspace.application.dto.response.TeamspaceInfoResponse;
import one.colla.teamspace.application.dto.response.TeamspaceParticipantsResponse;
import one.colla.teamspace.application.dto.response.TeamspaceSettingsResponse;
import one.colla.teamspace.domain.TagRepository;
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
	private final TagRepository tagRepository;
	private final ApplicationEventPublisher publisher;
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
	public TeamspaceInfoResponse readInfoByCode(CustomUserDetails userDetails, String inviteCode) {
		Long teamspaceId = inviteCodeService.getTeamspaceIdByCode(inviteCode);

		User user = null;
		if (userDetails != null) {
			user = userRepository.findById(userDetails.getUserId())
				.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_USER));
		}

		Teamspace teamspace = teamspaceRepository.findById(teamspaceId)
			.orElseThrow(() -> new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE));

		boolean isParticipatedUser =
			(user != null && userTeamspaceRepository.existsByUserAndTeamspace(user, teamspace));

		log.info("팀스페이스 정보조회 - 팀스페이스 Id: {}, 조회 사용자 Id: {}", teamspace.getId(), user != null ? user.getId() : "None");

		return TeamspaceInfoResponse.of(isParticipatedUser, teamspace);
	}

	@Transactional
	public InviteCodeResponse getInviteCode(CustomUserDetails userDetails, Long teamspaceId) {
		Pair<InviteCode, UserTeamspace> pair = generateAndSaveInviteCodeByTeamspaceId(userDetails, teamspaceId);
		InviteCode inviteCode = pair.getLeft();

		log.info("초대코드 생성(Copy) - 팀스페이스 Id: {}, 초대코드: {}", teamspaceId, inviteCode.getCode());
		return InviteCodeResponse.from(inviteCode);
	}

	@Transactional
	public void sendInviteCode(CustomUserDetails userDetails, Long teamspaceId, SendMailInviteCodeRequest request) {
		Pair<InviteCode, UserTeamspace> pair = generateAndSaveInviteCodeByTeamspaceId(userDetails, teamspaceId);
		InviteCode inviteCode = pair.getLeft();
		UserTeamspace userTeamspace = pair.getRight();
		String inviterName = userTeamspace.getUser().getUsernameValue();
		String teamspaceName = userTeamspace.getTeamspace().getNameValue();

		publisher.publishEvent(
			new InviteCodeSendMailEvent(request.email(), teamspaceName, inviterName, inviteCode.getCode())
		);

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

	@Transactional(readOnly = true)
	public TeamspaceParticipantsResponse getParticipants(CustomUserDetails userDetails, Long teamspaceId) {
		UserTeamspace userTeamspace = getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();

		List<ParticipantDto> participants = getParticipantByTeamspace(teamspace);
		return TeamspaceParticipantsResponse.from(participants);
	}

	@Transactional(readOnly = true)
	public TeamspaceSettingsResponse getSettings(CustomUserDetails userDetails, Long teamspaceId) {
		UserTeamspace userTeamspace = getUserTeamspace(userDetails, teamspaceId);

		if (userTeamspace.getTeamspaceRole() != TeamspaceRole.LEADER) {
			throw new CommonException(ExceptionCode.ONLY_LEADER_ACCESS);
		}

		Teamspace teamspace = userTeamspace.getTeamspace();
		List<ParticipantDto> participants = getParticipantByTeamspace(teamspace);

		return TeamspaceSettingsResponse.of(teamspace, participants);
	}

	private Pair<InviteCode, UserTeamspace> generateAndSaveInviteCodeByTeamspaceId(CustomUserDetails userDetails,
		Long teamspaceId) {
		UserTeamspace userTeamspace = getUserTeamspace(userDetails, teamspaceId);

		int validSeconds = VALID_HOURS * SECONDS_PER_HOUR;

		String generatedCode = "";
		boolean exists = true;

		do {
			generatedCode = randomCodeGenerator.generateRandomString(INVITE_CODE_LENGTH);
			exists = inviteCodeService.existsByCode(generatedCode);
		} while (exists);

		InviteCode inviteCode = inviteCodeService.saveInviteCode(
			InviteCode.of(generatedCode, userTeamspace.getTeamspace().getId(), validSeconds));

		return Pair.of(inviteCode, userTeamspace);
	}

	public UserTeamspace getUserTeamspace(CustomUserDetails userDetails, Long teamspaceId) {
		return userTeamspaceRepository.findByUserIdAndTeamspaceId(userDetails.getUserId(),
			teamspaceId).orElseThrow(() -> new CommonException(ExceptionCode.FORBIDDEN_TEAMSPACE)
		);
	}

	private List<ParticipantDto> getParticipantByTeamspace(Teamspace teamspace) {
		List<UserTeamspace> userTeamspaces = userTeamspaceRepository.findAllByTeamspace(teamspace);
		return userTeamspaces.stream()
			.map(ut -> ParticipantDto.of(ut.getUser(), ut, ut.getTag()))
			.toList();
	}
}

