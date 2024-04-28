package one.colla.teamspace.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.chat.domain.ChatChannel;
import one.colla.common.domain.BaseEntity;
import one.colla.feed.common.domain.Feed;
import one.colla.file.domain.Attachment;
import one.colla.schedule.domain.CalendarEvent;
import one.colla.teamspace.domain.vo.ProfileImageUrl;
import one.colla.teamspace.domain.vo.TeamspaceName;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "teamspaces")
public class Teamspace extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private TeamspaceName teamspaceName;

	@Embedded
	private ProfileImageUrl profileImageUrl;

	@OneToMany(mappedBy = "teamspace", fetch = FetchType.LAZY)
	private final List<UserTeamspace> userTeamspaces = new ArrayList<>();

	@OneToMany(mappedBy = "teamspace", fetch = FetchType.LAZY)
	private final List<Tag> tags = new ArrayList<>();

	@OneToMany(mappedBy = "teamspace", fetch = FetchType.LAZY)
	private final List<Attachment> attachments = new ArrayList<>();

	@OneToMany(mappedBy = "teamspace", fetch = FetchType.LAZY)
	private final List<ChatChannel> chatChannels = new ArrayList<>();

	@OneToMany(mappedBy = "teamspace", fetch = FetchType.LAZY)
	private final List<CalendarEvent> calendarEvents = new ArrayList<>();

	@OneToMany(mappedBy = "teamspace", fetch = FetchType.LAZY)
	private final List<Feed> feeds = new ArrayList<>();

	private Teamspace(TeamspaceName name) {
		this.teamspaceName = name;
	}

	public static Teamspace from(String teamspaceName) {
		TeamspaceName name = TeamspaceName.from(teamspaceName);
		return new Teamspace(name);
	}

	public String getTeamspaceNameValue() {
		return teamspaceName.getValue();
	}

	public String getProfileImageUrlValue() {
		return profileImageUrl != null ? profileImageUrl.getValue() : null;
	}
}
