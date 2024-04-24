package one.colla.infra.redis.invite;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import one.colla.common.util.RandomCodeGenerator;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@Service
@RequiredArgsConstructor
public class InviteCodeService {
	private static final int INVITE_CODE_LENGTH = 10;
	private static final int SECONDS_PER_HOUR = 3_600;
	private final InviteCodeRepository inviteCodeRepository;

	private final RandomCodeGenerator randomCodeGenerator;

	public String generateInviteCode(long teamspaceId, int validHours) {
		int validSeconds = validHours * SECONDS_PER_HOUR;

		String generatedCode = "";
		boolean exists = true;

		do {
			generatedCode = randomCodeGenerator.generateRandomString(INVITE_CODE_LENGTH);
			exists = inviteCodeRepository.existsByCode(generatedCode);
		} while (exists);

		InviteCode inviteCode = InviteCode.of(generatedCode, teamspaceId, validSeconds);
		inviteCodeRepository.save(inviteCode);
		return generatedCode;
	}

	public Long getTeamspaceIdByCode(String code) {
		InviteCode inviteCode = inviteCodeRepository.findByCode(code)
			.orElseThrow(() -> new CommonException(ExceptionCode.INVALID_OR_EXPIRED_INVITATION_CODE));

		return inviteCode.getTeamspaceId();
	}

}
