package one.colla.auth.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.payload.JsonFieldType;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import one.colla.auth.application.AuthService;
import one.colla.auth.application.OAuthService;
import one.colla.auth.application.dto.JwtPair;
import one.colla.auth.application.dto.request.LoginRequest;
import one.colla.auth.application.dto.request.OAuthLoginRequest;
import one.colla.auth.application.dto.request.RegisterRequest;
import one.colla.auth.application.dto.request.VerificationCheckRequest;
import one.colla.auth.application.dto.request.VerifyMailSendRequest;
import one.colla.auth.application.dto.response.LoginResponse;
import one.colla.auth.application.dto.response.OauthLoginUrlResponse;
import one.colla.auth.config.GoogleOAuthProperties;
import one.colla.auth.config.OAuthProperties;
import one.colla.auth.config.OAuthPropertyFactory;
import one.colla.common.ControllerTest;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.WithMockAnonymous;
import one.colla.common.util.CookieUtil;
import one.colla.common.util.oauth.OAuthUriGenerator;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.user.domain.OauthProvider;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {

	final ApiResponse<Object> SUCCESS_RESPONSE = ApiResponse.createSuccessResponse(Map.of());

	@MockBean
	CookieUtil cookieUtil;

	@MockBean
	AuthService authService;

	@MockBean
	OAuthService oAuthService;

	@MockBean
	OAuthPropertyFactory oAuthPropertyFactory;

	@MockBean
	OAuthUriGenerator oAuthUriGenerator;

	private static final Long USER_ID = 1L;
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final String COOKIE_STRING = "refreshToken=your-refresh-token-value-here; Path=/; Max-Age=604800";
	private static final int REFRESH_TOKEN_EXPIRES_IN_DAYS = 7;
	private static final JwtPair tokens = new JwtPair(ACCESS_TOKEN, REFRESH_TOKEN);
	private static final Pair<Long, JwtPair> pair = Pair.of(USER_ID, tokens);
	private static final ResponseCookie cookie = mock(ResponseCookie.class);
	private static final String EMAIL = "test@gmail.com";

	@Nested
	@DisplayName("소셜 회원가입/로그인 시")
	class OAuthDocs {

		final String URL = "example.com";
		final OauthProvider oauthProvider = OauthProvider.GOOGLE;
		final OAuthProperties oAuthProperties = new GoogleOAuthProperties();
		final OauthLoginUrlResponse oauthLoginUrlResponse = new OauthLoginUrlResponse(URL);

		@Test
		@DisplayName("요청받은 provider에 해당하는 oauth url을 응답받을 수 있다.")
		@WithMockAnonymous
		void getOAuthUrl() throws Exception {

			given(oAuthPropertyFactory.createOAuthProperty(oauthProvider))
				.willReturn(oAuthProperties);
			given(oAuthUriGenerator.generate(oAuthProperties))
				.willReturn(oauthLoginUrlResponse);

			mockMvc.perform(get("/api/v1/auth/oauth/{provider}/login", oauthProvider).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createSuccessResponse(oauthLoginUrlResponse))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller/Oauth")
						.description("Provider에 해당하는 Oauth 로그인 uri를 생성 합니다.")
						.pathParameters(
							parameterWithName("provider").description("OAuth 공급자 ex) GOOGLE, KAKAO, NAVER")
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.OBJECT),
							fieldWithPath("content.oauthLoginUrl").description("oauthLoginUrl")
								.type(JsonFieldType.STRING),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.NULL)
						)
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}

		@Test
		@DisplayName("요청받은 Authorization code로 회원가입/로그인을 진행할 수 있다.")
		@WithMockAnonymous
		void oauthRegisterOrLogin() throws Exception {

			final String CODE = "authCode";
			final OAuthLoginRequest oAuthLoginRequest = new OAuthLoginRequest(CODE);

			given(oAuthService.createToken(oAuthLoginRequest, oauthProvider))
				.willReturn(pair);

			given(cookieUtil.createCookie(REFRESH_TOKEN, tokens.refreshToken(),
				Duration.ofDays(REFRESH_TOKEN_EXPIRES_IN_DAYS).toSeconds())
			).willReturn(cookie);

			given(cookie.toString()).willReturn(COOKIE_STRING);

			mockMvc.perform(post("/api/v1/auth/oauth/{provider}/code", oauthProvider).with(csrf())
					.content(objectMapper.writeValueAsString(oAuthLoginRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createSuccessResponse(LoginResponse.of(tokens.accessToken(), USER_ID))))
				)
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller/Oauth")
						.description("Authorization code를 이용해 소셜 회원가입/로그인을 진행합니다.")
						.pathParameters(
							parameterWithName("provider").description("OAuth 공급자 ex) GOOGLE, KAKAO, NAVER")
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.OBJECT),
							fieldWithPath("content.accessToken").description("Access token").type(JsonFieldType.STRING),
							fieldWithPath("content.userId").description("User ID").type(JsonFieldType.NUMBER),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.NULL)
						)
						.requestSchema(Schema.schema("OAuthLoginRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build())
				));
		}
	}

	@Nested
	@DisplayName("회원가입 문서화")
	class RegistrationDocs {

		final String USERNAME = "testUsername";
		final String PASSWORD = "passWord1";
		final String VERIFY_CODE = "ABCDEFG";

		@DisplayName("회원가입 성공")
		@WithMockAnonymous
		@Test
		void registerSuccess() throws Exception {
			final RegisterRequest registerRequest = new RegisterRequest(USERNAME, PASSWORD, EMAIL, VERIFY_CODE);
			mockMvc.perform(post("/api/v1/auth/register").with(csrf())
					.content(objectMapper.writeValueAsString(registerRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createSuccessResponse(Map.of()))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("자체 회원가입을 수행합니다.")
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
		@WithMockAnonymous
		@Test
		void registerFailureDueToDuplicatedEmail() throws Exception {
			final RegisterRequest registerRequest = new RegisterRequest(USERNAME, PASSWORD, EMAIL, VERIFY_CODE);

			willThrow(new CommonException(ExceptionCode.DUPLICATED_USER_EMAIL)).given(authService)
				.register(any());

			mockMvc.perform(post("/api/v1/auth/register").with(csrf())
					.content(objectMapper.writeValueAsString(registerRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(new CommonException(ExceptionCode.DUPLICATED_USER_EMAIL)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("자체 회원가입을 수행합니다.")
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
		@WithMockAnonymous
		@Test
		void registerFailureDueToMismatchedVerificationCode() throws Exception {
			final RegisterRequest registerRequest = new RegisterRequest(USERNAME, PASSWORD, EMAIL, VERIFY_CODE);

			willThrow(new CommonException(ExceptionCode.UNAUTHORIZED_OR_EXPIRED_VERIFY_TOKEN)).given(authService)
				.register(any());

			mockMvc.perform(post("/api/v1/auth/register").with(csrf())
					.content(objectMapper.writeValueAsString(registerRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(
						new CommonException(ExceptionCode.UNAUTHORIZED_OR_EXPIRED_VERIFY_TOKEN)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("자체 회원가입을 수행합니다.")
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
	@DisplayName("자체 로그인 문서화")
	class SingInDocs {

		@Test
		@DisplayName("로그인 성공 - 사용자 이메일과 비밀번호가 일치 ")
		@WithMockAnonymous
		void signIn() throws Exception {

			final String PASSWORD = "testPassword123";
			final LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

			given(authService.login(loginRequest)).willReturn(pair);
			given(cookieUtil.createCookie("refreshToken", tokens.refreshToken(),
				Duration.ofDays(REFRESH_TOKEN_EXPIRES_IN_DAYS).toSeconds())).willReturn(cookie);
			given(cookie.toString()).willReturn(COOKIE_STRING);

			mockMvc.perform(post("/api/v1/auth/login").with(csrf())
					.content(objectMapper.writeValueAsString(loginRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createSuccessResponse(LoginResponse.of(tokens.accessToken(), USER_ID)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("자체 로그인을 수행합니다.")
						.requestFields(
							fieldWithPath("email").description("사용자 이메일").type(JsonFieldType.STRING),
							fieldWithPath("password").description("사용자 비밀번호").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.OBJECT),
							fieldWithPath("content.accessToken").description("성공적인 인증 후 제공되는 액세스 토큰")
								.type(JsonFieldType.STRING),
							fieldWithPath("content.userId").description("인증된 사용자의 ID").type(JsonFieldType.NUMBER),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.NULL)
						)
						.requestSchema(Schema.schema("LoginRequest"))
						.responseSchema(Schema.schema("ApiResponse"))
						.build()
					)
				)).andDo(print());
		}

		@Test
		@DisplayName("로그인 실패 - 사용자 이메일과 비밀번호가 불일치")
		@WithMockAnonymous
		void signInFailure() throws Exception {

			final String WRONG_PASSWORD = "wrongPassword";
			final LoginRequest loginRequest = new LoginRequest(EMAIL, WRONG_PASSWORD);

			given(authService.login(any(LoginRequest.class)))
				.willThrow(new CommonException(ExceptionCode.INVALID_EMAIL_OR_PASSWORD));

			mockMvc.perform(post("/api/v1/auth/login").with(csrf())
					.content(objectMapper.writeValueAsString(loginRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(ExceptionCode.INVALID_EMAIL_OR_PASSWORD))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("자체 로그인을 수행합니다.")
						.requestFields(
							fieldWithPath("email").description("사용자 이메일").type(JsonFieldType.STRING),
							fieldWithPath("password").description("사용자 비밀번호").type(JsonFieldType.STRING)
						)
						.responseFields(
							fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
							fieldWithPath("content").description("응답 내용").type(JsonFieldType.NULL),
							fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING)
						)
						.requestSchema(Schema.schema("LoginRequest"))
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
		@WithMockAnonymous
		@Test
		void sendVerifyMailSuccess() throws Exception {
			final VerifyMailSendRequest sendRequest = new VerifyMailSendRequest(EMAIL);

			mockMvc.perform(post("/api/v1/auth/mail/send").with(csrf())
					.content(objectMapper.writeValueAsString(sendRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createSuccessResponse(Map.of()))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("회원가입 인증 코드를 이메일로 전송합니다.")
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
		@WithMockAnonymous
		@Test
		void checkDuplication() throws Exception {

			mockMvc.perform(get("/api/v1/auth/mail/duplication").with(csrf().asHeader())
					.param("email", EMAIL))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(SUCCESS_RESPONSE)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("이메일 중복을 검사합니다.")
						.queryParameters(
							parameterWithName("email").description("중복조회 할 이메일")
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
		@WithMockAnonymous
		@Test
		void checkIsDuplicated() throws Exception {

			willThrow(new CommonException(ExceptionCode.DUPLICATED_USER_EMAIL)).given(authService)
				.checkDuplication(any());

			mockMvc.perform(get("/api/v1/auth/mail/duplication").with(csrf())
					.queryParam("email", EMAIL))
				.andExpect(status().isConflict())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(new CommonException(ExceptionCode.DUPLICATED_USER_EMAIL)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("이메일 중복을 검사합니다.")
						.queryParameters(
							parameterWithName("email").description("중복조회 할 이메일")
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
	@DisplayName("이메일 인증 문서화")
	class EmailVerificationDocs {
		final String EMAIL = "test@gmail.com";
		final String VERIFY_CODE = "AAAAAAA";

		@DisplayName("유효한 인증 코드로 검증 성공한다.")
		@WithMockAnonymous
		@Test
		void verifyEmailSuccess() throws Exception {

			final VerificationCheckRequest verificationRequest = new VerificationCheckRequest(EMAIL, VERIFY_CODE);

			mockMvc.perform(post("/api/v1/auth/mail/verification").with(csrf())
					.content(objectMapper.writeValueAsString(verificationRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(SUCCESS_RESPONSE)))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("이메일과 인증코드로 이메일을 인증합니다.")
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

		@DisplayName("유효하지 않은 인증 코드로 검증 실패한다.")
		@WithMockAnonymous
		@Test
		void verifyEmailFailure() throws Exception {
			final VerificationCheckRequest verificationRequest = new VerificationCheckRequest(EMAIL, VERIFY_CODE);

			willThrow(new CommonException(ExceptionCode.INVALID_VERIFY_TOKEN)).given(authService)
				.checkVerification(any());

			mockMvc.perform(post("/api/v1/auth/mail/verification").with(csrf())
					.content(objectMapper.writeValueAsString(verificationRequest))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(content().json(objectMapper.writeValueAsString(
					ApiResponse.createErrorResponse(new CommonException(ExceptionCode.INVALID_VERIFY_TOKEN)))))
				.andDo(restDocs.document(
					resource(ResourceSnippetParameters.builder()
						.tag("auth-controller")
						.description("이메일과 인증코드로 이메일을 인증합니다.")
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

}
