package one.colla.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth_approvals")
public class OauthApproval extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@Column(name = "provider", nullable = false)
	@Enumerated(EnumType.STRING)
	private OauthProvider oauthProvider;

	@Column(name = "access_token", nullable = false)
	private String accessToken;

	private OauthApproval(final User user, final OauthProvider oauthProvider, final String accessToken) {
		this.user = user;
		this.oauthProvider = oauthProvider;
		this.accessToken = accessToken;
	}

	public static OauthApproval createOAuthApproval(
		final User user,
		final OauthProvider oauthProvider,
		final String accessToken) {

		return new OauthApproval(user, oauthProvider, accessToken);
	}

	public void changeAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}

}
