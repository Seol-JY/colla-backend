package one.colla.infra.redis.invite;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import one.colla.common.util.RandomCodeGenerator;
import one.colla.global.exception.CommonException;

@ExtendWith(MockitoExtension.class)
class InviteCodeServiceTest {
	private static final int INVITE_CODE_LENGTH = 10;
	private static final String RANDOM_STRING = "RANDOMCODE";
	private static final Long TEAMSPACE_ID = 1L;
	private static final Long TTL = 100_000L;

	@Mock
	private InviteCodeRepository inviteCodeRepository;
	@Mock
	private RandomCodeGenerator randomCodeGenerator;
	@InjectMocks
	private InviteCodeService inviteCodeService;

	@Test
	@DisplayName("초대 코드 저장이 정상적으로 이루어진다.")
	void testSaveInviteCode() {
		// given
		InviteCode expectedInviteCode = InviteCode.of(RANDOM_STRING, TEAMSPACE_ID, TTL);
		when(inviteCodeRepository.save(any(InviteCode.class))).thenReturn(expectedInviteCode);

		// when
		InviteCode actualInviteCode = inviteCodeService.saveInviteCode(InviteCode.of(RANDOM_STRING, TEAMSPACE_ID, TTL));

		// then
		assertThat(actualInviteCode).isSameAs(expectedInviteCode);
		verify(inviteCodeRepository).save(any(InviteCode.class));
	}

	@Test
	@DisplayName("유효한 초대 코드로 팀스페이스 ID 조회가 정상적으로 이루어진다.")
	void testGetTeamspaceIdByCode_ReturnsTeamspaceId() {
		// given
		InviteCode inviteCode = InviteCode.of(RANDOM_STRING, TEAMSPACE_ID, TTL);
		when(inviteCodeRepository.findByCode(RANDOM_STRING)).thenReturn(Optional.of(inviteCode));

		// when
		Long actualTeamspaceId = inviteCodeService.getTeamspaceIdByCode(RANDOM_STRING);

		// then
		assertThat(actualTeamspaceId).isEqualTo(TEAMSPACE_ID);
	}

	@Test
	@DisplayName("유효하지 않거나 만료된 초대 코드로 팀스페이스 ID 조회 시 예외를 던진다.")
	void testGetTeamspaceIdByCode_ThrowsExceptionOnInvalidCode() {
		// given
		when(inviteCodeRepository.findByCode("INVALID_CODE")).thenReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> inviteCodeService.getTeamspaceIdByCode("INVALID_CODE"))
			.isExactlyInstanceOf(CommonException.class);
	}

}
