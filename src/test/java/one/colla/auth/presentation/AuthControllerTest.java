package one.colla.auth.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import one.colla.auth.application.AuthService;
import one.colla.auth.application.dto.request.DuplicationCheckRequest;
import one.colla.auth.application.dto.request.RegisterRequest;
import one.colla.auth.application.dto.request.VerificationCheckRequest;
import one.colla.auth.application.dto.request.VerifyMailSendRequest;
import one.colla.common.ControllerTest;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.util.CookieUtil;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {

	final ApiResponse<Object> SUCCESS_RESPONSE = ApiResponse.createSuccessResponse(Map.of());

	@MockBean
	CookieUtil cookieUtil;

	@MockBean
	AuthService authService;

	@Nested
	@DisplayName("회원가입 문서화")
	class RegistrationDocs {

		final String EMAIL = "test@gmail.com";
		final String USERNAME = "testUsername";
		final String PASSWORD = "passWord1";
		final String VERIFY_CODE = "ABCDEFG";

		@DisplayName("회원가입 성공")
		@Test
		void registerSuccess() throws Exception {
			final RegisterRequest registerRequest = new RegisterRequest(USERNAME, PASSWORD, EMAIL, VERIFY_CODE);

			mockMvc.perform(post("/api/v1/auth/register")
					.content(objectMapper.writeValueAsString(registerRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createSuccessResponse(Map.of()))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.requestFields(
							fieldWithPath("email").description("이메일").type(JsonFieldType.STRING),
							fieldWithPath("username").description("사용자 이름").type(JsonFieldType.STRING),
							fieldWithPath("password").description("비밀번호").type(JsonFieldType.STRING),
							fieldWithPath("verifyCode").description("검증 코드").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.OBJECT),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.NULL)
						)
						.requestSchema(Schema.schema("RegisterRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}

		@DisplayName("회원가입 실패 - 중복된 이메일")
		@Test
		void registerFailureDueToDuplicatedEmail() throws Exception {
			final RegisterRequest registerRequest = new RegisterRequest(USERNAME, PASSWORD, EMAIL, VERIFY_CODE);

			willThrow(new CommonException(ExceptionCode.DUPLICATED_USER_EMAIL)).given(authService)
				.register(any());

			mockMvc.perform(post("/api/v1/auth/register")
					.content(objectMapper.writeValueAsString(registerRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(new CommonException(ExceptionCode.DUPLICATED_USER_EMAIL)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.requestFields(
							fieldWithPath("email").description("이메일").type(JsonFieldType.STRING),
							fieldWithPath("username").description("사용자 이름").type(JsonFieldType.STRING),
							fieldWithPath("password").description("비밀번호").type(JsonFieldType.STRING),
							fieldWithPath("verifyCode").description("검증 코드").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.NULL),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING)
						)
						.requestSchema(Schema.schema("RegisterRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}

		@DisplayName("회원가입 실패 - 불일치하는 검증 코드")
		@Test
		void registerFailureDueToMismatchedVerificationCode() throws Exception {
			final RegisterRequest registerRequest = new RegisterRequest(USERNAME, PASSWORD, EMAIL, VERIFY_CODE);

			willThrow(new CommonException(ExceptionCode.UNAUTHORIZED_OR_EXPIRED_VERIFY_TOKEN)).given(authService)
				.register(any());

			mockMvc.perform(post("/api/v1/auth/register")
					.content(objectMapper.writeValueAsString(registerRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(
						new CommonException(ExceptionCode.UNAUTHORIZED_OR_EXPIRED_VERIFY_TOKEN)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.requestFields(
							fieldWithPath("email").description("이메일").type(JsonFieldType.STRING),
							fieldWithPath("username").description("사용자 이름").type(JsonFieldType.STRING),
							fieldWithPath("password").description("비밀번호").type(JsonFieldType.STRING),
							fieldWithPath("verifyCode").description("검증 코드").type(JsonFieldType.STRING).optional()
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.NULL),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING)
						)
						.requestSchema(Schema.schema("RegisterRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("이메일 발송 문서화")
	class EmailSendDocs {

		final String EMAIL = "test@gmail.com";

		@DisplayName("이메일 발송에 성공한다.")
		@Test
		void sendVerifyMailSuccess() throws Exception {
			final VerifyMailSendRequest sendRequest = new VerifyMailSendRequest(EMAIL);

			mockMvc.perform(post("/api/v1/auth/mail/send")
					.content(objectMapper.writeValueAsString(sendRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createSuccessResponse(Map.of()))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.requestFields(
							fieldWithPath("email").description("이메일").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.OBJECT),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.NULL)
						)
						.requestSchema(Schema.schema("VerifyMailSendRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}

	}

	@Nested
	@DisplayName("회원가입시 이메일 중복 검사 문서화")
	class EmailDuplicationDocs {

		final String EMAIL = "test@gmail.com";

		@DisplayName("중복된 이메일이 존재하지 않으면 성공한다.")
		@Test
		void checkDuplication() throws Exception {

			final DuplicationCheckRequest duplicationCheckRequest = new DuplicationCheckRequest(EMAIL);

			mockMvc.perform(post("/api/v1/auth/mail/duplication")
					.content(objectMapper.writeValueAsString(duplicationCheckRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(SUCCESS_RESPONSE)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.requestFields(
							fieldWithPath("email").description("이메일").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("code").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("content").type(JsonFieldType.OBJECT),
							fieldWithPath("message").description("message").type(JsonFieldType.NULL)
						)
						.requestSchema(Schema.schema("DuplicationCheckRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}

		@DisplayName("중복된 이메일이 존재하면 실패한다.")
		@Test
		void checkIsDuplicated() throws Exception {

			final DuplicationCheckRequest duplicationCheckRequest = new DuplicationCheckRequest(EMAIL);

			willThrow(new CommonException(ExceptionCode.DUPLICATED_USER_EMAIL)).given(authService)
				.checkDuplication(any());

			mockMvc.perform(post("/api/v1/auth/mail/duplication")
					.content(objectMapper.writeValueAsString(duplicationCheckRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(new CommonException(ExceptionCode.DUPLICATED_USER_EMAIL)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.requestFields(
							fieldWithPath("email").description("이메일").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("code").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("content").type(JsonFieldType.NULL),
							fieldWithPath("message").description("message").type(JsonFieldType.STRING)
						)
						.requestSchema(Schema.schema("DuplicationCheckRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}
	}

	@Nested
	@DisplayName("이메일 검증 문서화")
	class EmailVerificationDocs {
		final String EMAIL = "test@gmail.com";
		final String VERIFY_CODE = "AAAAAAA";

		@DisplayName("유효한 검증 코드로 검증 성공한다.")
		@Test
		void verifyEmailSuccess() throws Exception {

			final VerificationCheckRequest verificationRequest = new VerificationCheckRequest(EMAIL, VERIFY_CODE);

			mockMvc.perform(post("/api/v1/auth/mail/verification")
					.content(objectMapper.writeValueAsString(verificationRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(SUCCESS_RESPONSE)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.requestFields(
							fieldWithPath("email").description("이메일").type(JsonFieldType.STRING),
							fieldWithPath("verifyCode").description("검증 코드").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.OBJECT),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.NULL)
						)
						.requestSchema(Schema.schema("VerificationCheckRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}

		@DisplayName("유효하지 않은 검증 코드로 검증 실패한다.")
		@Test
		void verifyEmailFailure() throws Exception {
			final VerificationCheckRequest verificationRequest = new VerificationCheckRequest(EMAIL, VERIFY_CODE);

			willThrow(new CommonException(ExceptionCode.INVALID_VERIFY_TOKEN)).given(authService)
				.checkVerification(any());

			mockMvc.perform(post("/api/v1/auth/mail/verification")
					.content(objectMapper.writeValueAsString(verificationRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(new CommonException(ExceptionCode.INVALID_VERIFY_TOKEN)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.requestFields(
							fieldWithPath("email").description("이메일").type(JsonFieldType.STRING),
							fieldWithPath("verifyCode").description("검증 코드").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.NULL),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING)
						)
						.requestSchema(Schema.schema("VerificationCheckRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}
	}

	@Test
	void refresh() {
	}
}
