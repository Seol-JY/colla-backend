package one.colla.auth.application;

import static one.colla.common.fixtures.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import one.colla.auth.application.dto.JwtPair;
import one.colla.auth.application.dto.oauth.OAuthTokenResponse;
import one.colla.auth.application.dto.oauth.OAuthUserInfo;
import one.colla.auth.application.dto.request.OAuthLoginRequest;
import one.colla.auth.config.OAuthProperties;
import one.colla.auth.config.OAuthPropertyFactory;
import one.colla.common.ServiceTest;
import one.colla.user.domain.OauthApproval;
import one.colla.user.domain.OauthApprovalRepository;
import one.colla.user.domain.Provider;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;
import one.colla.user.domain.vo.Email;

public class OAuthServiceTest extends ServiceTest {

	private static final String AUTHORIZATION_CODE = "authorizationCode";
	private static final String OLD_ACCESS_TOKEN = "oldAccessToken";
	private static final String NEW_ACCESS_TOKEN = "newAccessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final String ID_TOKEN = "idToken";
	private static final String EMAIL = "email@example.com";
	private static final String NICKNAME = "nickname";
	private static final String PICTURE_URL = "http://prcture.com";
	private static final Provider EXIST_PROVIDER = Provider.GOOGLE;
	private static final Provider NOT_EXIST_PROVIDER = Provider.KAKAO;

	@Mock
	private OAuthPropertyFactory oAuthPropertyFactory;
	@Mock
	private OAuthUserCreator oAuthUserCreator;
	@Mock
	private UserRepository userRepository;
	@Mock
	private OAuthClient oAuthClient;
	@Mock
	private OauthApprovalRepository oauthApprovalRepository;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private OAuthService oAuthService;

	OAuthProperties oAuthProperties;
	OAuthTokenResponse oAuthTokenResponse;
	OAuthUserInfo oAuthUserInfo;
	OauthApproval oauthApproval;
	User user;
	JwtPair jwtPair;

	@BeforeEach
	public void setup() {
		oAuthProperties = mock(OAuthProperties.class);
		oAuthTokenResponse = new OAuthTokenResponse(NEW_ACCESS_TOKEN, REFRESH_TOKEN, ID_TOKEN);
		oAuthUserInfo = new OAuthUserInfo(EMAIL, NICKNAME, PICTURE_URL);
		jwtPair = JwtPair.of(NEW_ACCESS_TOKEN, REFRESH_TOKEN);
	}

	@DisplayName("기존 소셜 회원이 같은 Provider로 로그인 시 Access Token 업데이트")
	@Test
	public void whenExistingUserLogInWithSameProvider_UpdateAccessToken() {

		// given
		Provider provider = Provider.GOOGLE;
		user = testFixtureBuilder.buildUser(USER1());
		setupMock(provider, true, true);

		// when
		Pair<Long, JwtPair> response = oAuthService.createToken(new OAuthLoginRequest(AUTHORIZATION_CODE), provider);

		// then
		List<OauthApproval> oauthApprovalList = user.getOauthApprovals();
		Optional<OauthApproval> oauthApproval = oauthApprovalList.stream()
			.filter(approval -> approval.getProvider().equals(provider))
			.findFirst();

		assertSoftly(softly -> {
			softly.assertThat(response.getLeft()).isEqualTo(user.getId());
			softly.assertThat(response.getRight().accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
			softly.assertThat(response.getRight().refreshToken()).isEqualTo(REFRESH_TOKEN);
			softly.assertThat(oauthApprovalList).hasSize(1);
			softly.assertThat(oauthApproval).isPresent();
			softly.assertThat(oauthApproval.get().getProvider()).isEqualTo(provider);
			softly.assertThat(oauthApproval.get().getAccessToken()).isEqualTo(NEW_ACCESS_TOKEN);

		});

	}

	@DisplayName("기존 소셜 로그인 유저가 email은 같지만 다른 Provider로 소셜 로그인 시 새로운 Oauth Approval 생성")
	@Test
	public void whenExistingUserLogsInWithNewProvider_CreateOAuthApproval() {

		// given
		Provider provider = Provider.GOOGLE;
		user = testFixtureBuilder.buildUser(USER1());
		setupMock(provider, true, false);

		// when
		Pair<Long, JwtPair> response = oAuthService.createToken(new OAuthLoginRequest(AUTHORIZATION_CODE), provider);

		// then
		List<OauthApproval> findOauthApprovalList = user.getOauthApprovals();
		ArgumentCaptor<OauthApproval> approvalCaptor = ArgumentCaptor.forClass(OauthApproval.class);
		verify(oauthApprovalRepository).save(approvalCaptor.capture());
		OauthApproval savedApproval = approvalCaptor.getValue();

		assertSoftly(softly -> {
			softly.assertThat(response.getLeft()).isEqualTo(user.getId());
			softly.assertThat(response.getRight().accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
			softly.assertThat(response.getRight().refreshToken()).isEqualTo(REFRESH_TOKEN);
			softly.assertThat(findOauthApprovalList).hasSize(2);
			softly.assertThat(savedApproval.getProvider()).isEqualTo(provider);
			softly.assertThat(savedApproval.getAccessToken()).isEqualTo(NEW_ACCESS_TOKEN);

		});
	}

	@DisplayName("새 사용자 소셜 회원가입 처리")
	@Test
	public void whenNewUserRegistersWithSocialLogin_CreateUserAndOAuthApproval() {

		// given
		Provider provider = Provider.GOOGLE;
		user = User.createSocialUser(NICKNAME, EMAIL, PICTURE_URL);
		setupMock(provider, false, false);

		// when
		Pair<Long, JwtPair> response = oAuthService.createToken(new OAuthLoginRequest(AUTHORIZATION_CODE), provider);

		// then
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();

		assertSoftly(softly -> {
			softly.assertThat(response.getLeft()).isEqualTo(savedUser.getId());
			softly.assertThat(response.getRight().accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
			softly.assertThat(response.getRight().refreshToken()).isEqualTo(REFRESH_TOKEN);

			softly.assertThat(savedUser.getUsernameValue()).isEqualTo(oAuthUserInfo.nickname());
			softly.assertThat(savedUser.getEmailValue()).isEqualTo(oAuthUserInfo.email());
			softly.assertThat(savedUser.getProfileImageUrlValue()).isEqualTo(oAuthUserInfo.picture());

			softly.assertThat(savedUser.getOauthApprovals()).hasSize(1);
			softly.assertThat(savedUser.getOauthApprovals().get(0).getProvider()).isEqualTo(provider);
			softly.assertThat(savedUser.getOauthApprovals().get(0).getAccessToken()).isEqualTo(NEW_ACCESS_TOKEN);
		});

	}

	private void setupMock(Provider provider, boolean userExists, boolean providerExists) {

		if (userExists) {
			if (providerExists) {
				user.addOAuthApproval(OauthApproval.createOAuthApproval(user, EXIST_PROVIDER, OLD_ACCESS_TOKEN));
			} else {
				user.addOAuthApproval(OauthApproval.createOAuthApproval(user, NOT_EXIST_PROVIDER, OLD_ACCESS_TOKEN));
			}
			given(userRepository.findByEmail(new Email(EMAIL))).willReturn(Optional.of(user));
		} else {
			given(userRepository.findByEmail(new Email(EMAIL))).willReturn(Optional.empty());
		}
		given(oAuthPropertyFactory.createOAuthProperty(provider)).willReturn(oAuthProperties);
		given(oAuthClient.getAccessToken(oAuthProperties, AUTHORIZATION_CODE)).willReturn(oAuthTokenResponse);
		given(oAuthUserCreator.createUser(oAuthTokenResponse, provider)).willReturn(oAuthUserInfo);
		given(userRepository.save(user)).willReturn(user);
		given(jwtService.createToken(any(User.class))).willReturn(jwtPair);
		given(oauthApprovalRepository.save(oauthApproval)).willReturn(oauthApproval);
	}
}
