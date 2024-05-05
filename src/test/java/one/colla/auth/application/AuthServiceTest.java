package one.colla.auth.application;

import static one.colla.global.exception.ExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import one.colla.auth.application.dto.request.RegisterRequest;
import one.colla.auth.application.dto.request.VerificationCheckRequest;
import one.colla.auth.application.dto.request.VerifyMailSendRequest;
import one.colla.common.ServiceTest;
import one.colla.common.util.RandomCodeGenerator;
import one.colla.global.exception.CommonException;
import one.colla.infra.mail.events.VerifyCodeSendMailEvent;
import one.colla.infra.redis.verify.VerifyCode;
import one.colla.infra.redis.verify.VerifyCodeService;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;
import one.colla.user.domain.vo.Email;

class AuthServiceTest extends ServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private VerifyCodeService verifyCodeService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RandomCodeGenerator randomCodeGenerator;

	@Mock
	private ApplicationEventPublisher publisher;

	@InjectMocks
	private AuthService authService;

	final String USER_NAME = "testUsername";
	final String PASSWORD = "testPassword1";
	final String ENCODED_PASSWORD = "encodedPassword1";
	final String TARGET_EMAIL = "email@example.com";
	final String VERIFY_CODE = "ABCDEFG";
	final String INVALID_CODE = "ZZZZZZZ";
	final long TTL = 1200;

	@Nested
	@DisplayName("회원가입시")
	class RegisterTest {

		@Test
		@DisplayName("모든 조건이 만족 한다면 성공적으로 회원가입 할 수 있다.")
		void registerSuccessfully() {

			// given
			RegisterRequest request = new RegisterRequest(USER_NAME, PASSWORD, TARGET_EMAIL, VERIFY_CODE);
			given(userRepository.findByEmail(new Email(TARGET_EMAIL))).willReturn(Optional.empty());
			given(verifyCodeService.findByEmail(TARGET_EMAIL)).willReturn(
				Optional.of(VerifyCode.of(TARGET_EMAIL, VERIFY_CODE, TTL)));
			given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);

			// when
			authService.register(request);

			// then
			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			verify(userRepository).save(userCaptor.capture());
			User savedUser = userCaptor.getValue();
			assertThat(savedUser.getUsernameValue()).isEqualTo(USER_NAME);
			assertThat(savedUser.getPasswordValue()).matches(passwordEncoder.encode(PASSWORD));
			assertThat(savedUser.getEmail().getValue()).isEqualTo(TARGET_EMAIL);
		}

		@Test
		@DisplayName("이메일 중복 시 회원가입을 실패한다")
		void failRegistrationDueToEmailDuplication() {
			// given
			RegisterRequest request = new RegisterRequest(USER_NAME, PASSWORD, TARGET_EMAIL, VERIFY_CODE);
			given(userRepository.findByEmail(any())).willReturn(Optional.of(mock(User.class)));

			// when & then
			assertThatThrownBy(() -> authService.register(request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(DUPLICATED_USER_EMAIL.getMessage());
		}

		@Test
		@DisplayName("인증코드 불일치 시 회원가입을 실패한다")
		void failRegistrationDueToCodeMismatch() {
			// given
			RegisterRequest request = new RegisterRequest(USER_NAME, PASSWORD, TARGET_EMAIL, VERIFY_CODE);
			given(userRepository.findByEmail(any())).willReturn(Optional.empty());
			given(verifyCodeService.findByEmail(any())).willReturn(
				Optional.of(VerifyCode.of(TARGET_EMAIL, INVALID_CODE, TTL)));

			// when & then
			assertThatThrownBy(() -> authService.register(request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(UNAUTHORIZED_OR_EXPIRED_VERIFY_TOKEN.getMessage());
		}

		@Test
		@DisplayName("인증코드 만료 또는 존재하지 않을시 회원가입을 실패한다")
		void failRegistrationDueToCodeNotFound() {
			// given
			RegisterRequest request = new RegisterRequest(USER_NAME, PASSWORD, TARGET_EMAIL, VERIFY_CODE);
			given(userRepository.findByEmail(any())).willReturn(Optional.empty());
			given(verifyCodeService.findByEmail(any())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> authService.register(request))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(UNAUTHORIZED_OR_EXPIRED_VERIFY_TOKEN.getMessage());
		}
	}

	@Nested
	@DisplayName("회원가입 이메일 중복 체크시")
	class EmailDuplicationTest {

		@DisplayName("이메일이 유일하다면 어떠한 예외도 발생하지 않는다.")
		@Test
		void checkDuplicationSuccess() {

			// given
			final String UNIQUE_EMAIL = "unique@example.com";
			given(userRepository.findByEmail(new Email(UNIQUE_EMAIL))).willReturn(Optional.empty());

			// when & then
			assertThatCode(() -> authService.checkDuplication(UNIQUE_EMAIL)).doesNotThrowAnyException();
		}

		@DisplayName("이메일이 중복 됐다면 중복 예외를 발생한다.")
		@Test
		void checkDuplicationFail() {

			// given
			final String DUPLICATED_EMAIL = "duplicated@example.com";
			given(userRepository.findByEmail(new Email(DUPLICATED_EMAIL))).willReturn(Optional.of(mock(User.class)));

			// when & then
			assertThatCode(() -> authService.checkDuplication(DUPLICATED_EMAIL))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(DUPLICATED_USER_EMAIL.getMessage());
		}
	}

	@DisplayName("회원가입 인증코드 검증시")
	@Nested
	class VerifyCodeTest {

		@DisplayName("인증코드가 일치하면 어떠한 예외도 발생하지 않는다.")
		@Test
		void testCheckVerificationWithValidCode() {

			// Given
			VerificationCheckRequest dto = new VerificationCheckRequest(TARGET_EMAIL, VERIFY_CODE);
			given(verifyCodeService.findByEmail(TARGET_EMAIL))
				.willReturn(Optional.of(VerifyCode.of(TARGET_EMAIL, VERIFY_CODE, TTL)));

			// When & Then
			assertThatCode(() -> authService.checkVerification(dto))
				.doesNotThrowAnyException();
		}

		@DisplayName("인증코드가 일치하지 않으면 검증 예외가 발생한다.")
		@Test
		void testCheckVerificationWithInvalidCode() {

			// Given
			VerificationCheckRequest dto = new VerificationCheckRequest(TARGET_EMAIL, VERIFY_CODE);
			given(verifyCodeService.findByEmail(TARGET_EMAIL))
				.willReturn(Optional.of(VerifyCode.of(TARGET_EMAIL, INVALID_CODE, TTL)));

			// When & Then
			assertThatThrownBy(() -> authService.checkVerification(dto))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(INVALID_VERIFY_TOKEN.getMessage());
		}

		@Test
		@DisplayName("Redis 저장소에 인증코드가 존재하지 않으면 검증 예외가 발생한다.")
		void testCheckVerificationWithMissingCode() {

			// Given
			VerificationCheckRequest dto = new VerificationCheckRequest(TARGET_EMAIL, VERIFY_CODE);
			when(verifyCodeService.findByEmail(TARGET_EMAIL))
				.thenReturn(Optional.empty());

			// When & Then
			assertThatThrownBy(() -> authService.checkVerification(dto))
				.isInstanceOf(CommonException.class)
				.hasMessageContaining(INVALID_VERIFY_TOKEN.getMessage());
		}
	}

	@DisplayName("요청받은 이메일로 인증코드를 보낼 수 있다.")
	@Test
	void sendVerifyMail() {

		// given
		VerifyMailSendRequest request = new VerifyMailSendRequest(TARGET_EMAIL);
		given(randomCodeGenerator.generateRandomString(anyInt())).willReturn(VERIFY_CODE);
		willDoNothing().given(verifyCodeService).save(any(VerifyCode.class));
		ArgumentCaptor<VerifyCodeSendMailEvent> argumentCaptor = ArgumentCaptor.forClass(VerifyCodeSendMailEvent.class);

		// when
		authService.sendVerifyMail(request);

		// then
		verify(publisher, times(1)).publishEvent(argumentCaptor.capture());
		VerifyCodeSendMailEvent capturedEvent = argumentCaptor.getValue();
		assertThat(capturedEvent).isNotNull();
		assertThat(capturedEvent.email()).isEqualTo(TARGET_EMAIL);
		assertThat(capturedEvent.verifyCode()).isEqualTo(VERIFY_CODE);

	}
}
