package one.colla.infra.redis.invite;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
	@Mock
	private InviteCodeRepository inviteCodeRepository;
	@Mock
	private RandomCodeGenerator randomCodeGenerator;
	@InjectMocks
	private InviteCodeService inviteCodeService;

	@BeforeEach
	void setUp() {
		lenient().when(randomCodeGenerator.generateRandomString(INVITE_CODE_LENGTH))
			.thenReturn(RANDOM_STRING);
	}

	@Test
	@DisplayName("초대 코드 생성이 정상적으로 이루어진다.")
	void testGenerateInviteCode() {
		// given
		when(inviteCodeRepository.existsByCode(RANDOM_STRING)).thenReturn(false);

		// when
		String code = inviteCodeService.generateInviteCode(123, 24);

		// then
		assertThat(RANDOM_STRING).isEqualTo(code);
	}

	@Test
	@DisplayName("코드가 이미 존재할 경우, 새 코드를 생성하여 반환한다.")
	void testGenerateInviteCode_RetryOnDuplicate() {
		// given
		when(inviteCodeRepository.existsByCode(RANDOM_STRING)).thenReturn(true, false);
		when(randomCodeGenerator.generateRandomString(INVITE_CODE_LENGTH))
			.thenReturn(RANDOM_STRING)
			.thenReturn("NEWCODE123");

		// when
		String newCode = inviteCodeService.generateInviteCode(123, 24);

		// then
		assertThat(newCode).isEqualTo("NEWCODE123");
		verify(inviteCodeRepository, times(2)).existsByCode(anyString()); // 두 번 확인함을 검증
		verify(inviteCodeRepository).save(any(InviteCode.class)); // 저장 로직 호출 확인
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
