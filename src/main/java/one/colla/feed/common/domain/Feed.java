package one.colla.feed.common.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.BaseEntity;
import one.colla.schedule.domain.CalendarEventFeedLink;
import one.colla.teamspace.domain.Teamspace;
import one.colla.user.domain.User;

@Getter
@Entity
@DiscriminatorColumn(name = "feed_type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feeds")
public abstract class Feed extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teamspace_id", nullable = false, updatable = false)
	private Teamspace teamspace;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@OneToOne(mappedBy = "feed", cascade = CascadeType.ALL)
	private CalendarEventFeedLink calendarEventFeedLink;

	@OneToMany(mappedBy = "feed", fetch = FetchType.LAZY)
	private final List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "feed", fetch = FetchType.LAZY)
	private final List<FeedAttachment> feedAttachments = new ArrayList<>();

}
