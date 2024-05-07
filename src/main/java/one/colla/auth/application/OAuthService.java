package one.colla.auth.application;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.auth.application.dto.JwtPair;
import one.colla.auth.application.dto.oauth.OAuthTokenResponse;
import one.colla.auth.application.dto.oauth.OAuthUserInfo;
import one.colla.auth.application.dto.request.OAuthLoginRequest;
import one.colla.auth.config.OAuthProperties;
import one.colla.auth.config.OAuthPropertyFactory;
import one.colla.user.domain.OauthApproval;
import one.colla.user.domain.OauthApprovalRepository;
import one.colla.user.domain.OauthProvider;
import one.colla.user.domain.User;
import one.colla.user.domain.UserRepository;
import one.colla.user.domain.vo.Email;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OAuthService {

	private final OAuthPropertyFactory oAuthPropertyFactory;
	private final OAuthUserCreator oAuthUserCreator;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final OAuthClient oAuthClient;
	private final OauthApprovalRepository oauthApprovalRepository;

	public Pair<Long, JwtPair> createToken(final OAuthLoginRequest dto, final OauthProvider oauthProvider) {
		OAuthProperties oAuthProperties = oAuthPropertyFactory.createOAuthProperty(oauthProvider);
		OAuthTokenResponse tokenResponse = oAuthClient.getAccessToken(oAuthProperties, dto.code());
		OAuthUserInfo oAuthUserInfo = oAuthUserCreator.createUser(tokenResponse, oauthProvider);
		User user = findOrCreateUser(oAuthUserInfo, oauthProvider, tokenResponse.accessToken());
		JwtPair jwtPair = jwtService.createToken(user);
		return Pair.of(user.getId(), jwtPair);
	}

	private User findOrCreateUser(final OAuthUserInfo oAuthUserInfo, final OauthProvider oauthProvider,
		final String accessToken) {
		return userRepository.findByEmail(new Email(oAuthUserInfo.email()))
			.map(user -> updateUserWithOAuthApproval(user, oauthProvider, accessToken))
			.orElseGet(() -> registerNewUser(oAuthUserInfo, oauthProvider, accessToken));
	}

	private User registerNewUser(OAuthUserInfo oAuthUserInfo, OauthProvider oauthProvider, String accessToken) {
		User user = User.createSocialUser(oAuthUserInfo.nickname(), oAuthUserInfo.email(), oAuthUserInfo.picture());
		userRepository.save(user);
		addOAuthApproval(user, oauthProvider, accessToken);
		log.info("소셜 회원가입 - 유저 Id: {}", user.getId());
		return user;
	}

	private User updateUserWithOAuthApproval(User user, OauthProvider oauthProvider, String accessToken) {
		user.getOauthApprovals().stream()
			.filter(approval -> approval.getOauthProvider().equals(oauthProvider))
			.findFirst()
			.ifPresentOrElse(
				approval -> updateAccessToken(approval, accessToken),
				() -> addOAuthApproval(user, oauthProvider, accessToken));

		log.info("소셜 로그인 - 유저 Id: {}", user.getId());
		return user;
	}

	private void updateAccessToken(OauthApproval approval, String accessToken) {
		approval.changeAccessToken(accessToken);
		// log.info("유저 {}의 {} Access token이 업데이트 되었습니다.", approval.getUser().getId(), approval.getProvider());
	}

	private void addOAuthApproval(User user, OauthProvider oauthProvider, String accessToken) {
		OauthApproval oauthApproval = OauthApproval.createOAuthApproval(user, oauthProvider, accessToken);
		user.addOAuthApproval(oauthApproval);
		oauthApprovalRepository.save(oauthApproval);

		log.info("계정이 {}와 연동 되었습니다. - 유저 Id: {}", oauthProvider, user.getId());
	}
}
