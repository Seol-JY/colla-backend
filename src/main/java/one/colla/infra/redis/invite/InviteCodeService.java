package one.colla.infra.redis.invite;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@Service
@RequiredArgsConstructor
public class InviteCodeService {
	private final InviteCodeRepository inviteCodeRepository;

	public InviteCode saveInviteCode(InviteCode inviteCode) {
		return inviteCodeRepository.save(inviteCode);
	}

	public Long getTeamspaceIdByCode(String code) {
		InviteCode inviteCode = inviteCodeRepository.findByCode(code)
			.orElseThrow(() -> new CommonException(ExceptionCode.INVALID_OR_EXPIRED_INVITATION_CODE));

		return inviteCode.getTeamspaceId();
	}

	public boolean existsByCode(String generatedCode) {
		return inviteCodeRepository.existsByCode(generatedCode);
	}
}
