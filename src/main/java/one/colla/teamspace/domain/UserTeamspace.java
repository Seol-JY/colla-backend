package one.colla.teamspace.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.BaseEntity;
import one.colla.common.domain.CompositeKeyBase;
import one.colla.user.domain.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_teamspace")
public class UserTeamspace extends BaseEntity {

	@EmbeddedId
	private UserTeamspaceId userTeamspaceId;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@MapsId("teamspaceId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teamspace_id", nullable = false)
	private Teamspace teamspace;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id")
	private Tag tag;

	public static class UserTeamspaceId extends CompositeKeyBase {
		@Column(name = "user_id")
		private Long userId;

		@Column(name = "teamspace_id")
		private Long teamspaceId;
	}
}
