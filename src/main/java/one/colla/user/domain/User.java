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
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.UserChatChannel;
import one.colla.common.domain.BaseEntity;
import one.colla.feed.common.domain.Comment;
import one.colla.feed.common.domain.Feed;
import one.colla.file.domain.Attachment;
import one.colla.schedule.domain.CalendarEventSubtodo;
import one.colla.schedule.domain.UserCalendarEvent;
import one.colla.schedule.domain.UserCalendarEventMention;
import one.colla.teamspace.domain.UserTeamspace;

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

}
