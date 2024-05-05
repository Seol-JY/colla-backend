package one.colla.teamspace.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.domain.BaseEntity;
import one.colla.common.domain.CompositeKeyBase;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.user.domain.User;

@Slf4j
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_teamspaces")
public class UserTeamspace extends BaseEntity {
	@EmbeddedId
	private UserTeamspaceId userTeamspaceId = new UserTeamspaceId();

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@MapsId("teamspaceId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teamspace_id", nullable = false, updatable = false)
	private Teamspace teamspace;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id")
	private Tag tag;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private TeamspaceRole teamspaceRole;

	private UserTeamspace(User user, Teamspace teamspace, TeamspaceRole teamspaceRole) {
		this.user = user;
		this.teamspace = teamspace;
		this.teamspaceRole = teamspaceRole;
	}

	public static UserTeamspace of(User user, Teamspace teamspace, TeamspaceRole teamspaceRole) {
		return new UserTeamspace(user, teamspace, teamspaceRole);
	}

	public void changeTag(Tag newTag) {
		if (!newTag.getTeamspace().equals(this.teamspace)) {
			log.info("팀 스페이스 설정 업데이트 실패(해당 팀스페이스의 태그가 아님) - 팀 스페이스 Id: {}, 사용자 Id: {}",
				this.teamspace.getId(), this.getUser().getId());
			throw new CommonException(ExceptionCode.FAIL_CHANGE_USERTAG);
		}
		if (this.tag != null) {
			this.tag.getUserTeamspaces().remove(this);
		}
		this.tag = newTag;
		newTag.getUserTeamspaces().add(this);
	}

	private static class UserTeamspaceId extends CompositeKeyBase {
		@Column(name = "user_id")
		private Long userId;

		@Column(name = "teamspace_id")
		private Long teamspaceId;
	}
}
