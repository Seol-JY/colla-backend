package one.colla.user.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.UserChatChannel;
import one.colla.common.domain.BaseEntity;
import one.colla.feed.collect.domain.CollectFeedResponse;
import one.colla.feed.common.domain.Comment;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.scheduling.domain.SchedulingFeedAvailableTime;
import one.colla.feed.vote.domain.VoteFeedSelection;
import one.colla.file.domain.Attachment;
import one.colla.schedule.domain.CalendarEventSubtodo;
import one.colla.schedule.domain.UserCalendarEvent;
import one.colla.schedule.domain.UserCalendarEventMention;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.TeamspaceRole;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.vo.Email;
import one.colla.user.domain.vo.ProfileImageUrl;
import one.colla.user.domain.vo.Username;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Embedded
	private Username username;

	@Column(name = "password")
	private String password;

	@Embedded
	private Email email;

	@Column(name = "email_subscription", nullable = false)
	private boolean emailSubscription = true;

	@Embedded
	private ProfileImageUrl profileImageUrl;

	@Column(name = "comment_notification", nullable = false)
	@Enumerated(EnumType.STRING)
	private CommentNotification commentNotification;

	private User(Username username, String password, Email email, ProfileImageUrl profileImageUrl) {
		this.userRole = UserRole.USER;
		this.username = username;
		this.password = password;
		this.email = email;
		this.commentNotification = CommentNotification.ALL;
		this.profileImageUrl = profileImageUrl;
	}

	public static User createGeneralUser(String createUsername, String createPassword, String createEmail) {
		Username username = Username.from(createUsername);
		Email email = Email.from(createEmail);
		return new User(username, createPassword, email, null);
	}

	public static User createSocialUser(String createUsername, String createEmail, String createProfileImageUrl) {
		Username username = Username.from(createUsername);
		Email email = Email.from(createEmail);
		ProfileImageUrl profileImageUrl = ProfileImageUrl.from(createProfileImageUrl);
		return new User(username, null, email, profileImageUrl);
	}

	public void addOAuthApproval(final OauthApproval oauthApproval) {
		this.oauthApprovals.add(oauthApproval);
	}

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<OauthApproval> oauthApprovals = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<UserTeamspace> userTeamspaces = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<UserChatChannel> userChatChannels = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<ChatChannelMessage> chatChannelMessages = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<UserCalendarEventMention> userCalendarEventMentions = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<UserCalendarEvent> userCalendarEvents = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<CalendarEventSubtodo> calendarEventSubtodos = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<Attachment> attachments = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<Feed> feeds = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<CollectFeedResponse> collectFeedResponses = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<VoteFeedSelection> voteFeedSelections = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final List<SchedulingFeedAvailableTime> schedulingFeedAvailableTimes = new ArrayList<>();

	public String getUsernameValue() {
		return username.getValue();
	}

	public String getEmailValue() {
		return email.getValue();
	}

	public String getProfileImageUrlValue() {
		return profileImageUrl != null ? profileImageUrl.getValue() : null;
	}

	public void updateProfileImage(ProfileImageUrl newProfileImageUrl) {
		this.profileImageUrl = newProfileImageUrl;
	}

	public UserTeamspace participate(
		final Teamspace teamspace,
		final TeamspaceRole teamspaceRole
	) {
		UserTeamspace userTeamspace = UserTeamspace.of(this, teamspace, teamspaceRole);
		userTeamspaces.add(userTeamspace);
		teamspace.addUserTeamspace(userTeamspace);
		return userTeamspace;
	}
}
