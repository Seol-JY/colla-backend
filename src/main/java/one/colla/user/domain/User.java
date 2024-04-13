package one.colla.user.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<OauthApproval> oauthApprovals = new ArrayList<>();

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "username", nullable = false, length = 50)
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "email_notification", nullable = false)
	private boolean emailSubscription = true;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "comment_notification", nullable = false)
	@Enumerated(EnumType.STRING)
	private CommentNotification commentNotification;

}
