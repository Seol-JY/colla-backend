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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.BaseEntity;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;
import one.colla.file.domain.Attachment;
import one.colla.file.domain.AttachmentType;
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

	@OneToMany(mappedBy = "feed", fetch = FetchType.LAZY, orphanRemoval = true)
	@OrderBy("createdAt ASC")
	private final List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "feed", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private final List<FeedAttachment> feedAttachments = new ArrayList<>();

	protected Feed(
		final User user,
		final Teamspace teamspace,
		final CommonCreateFeedRequest<? extends CreateFeedDetails> commonCreateFeedRequest
	) {
		setUser(user);
		setTeamspace(teamspace);
		this.title = commonCreateFeedRequest.title();

		if (commonCreateFeedRequest.images() != null) {
			List<Attachment> imageFiles =
				createAttachments(commonCreateFeedRequest.images(), user, teamspace, AttachmentType.IMAGE);
			addAttachments(imageFiles);
		}

		if (commonCreateFeedRequest.attachments() != null) {
			List<Attachment> attachmentFiles =
				createAttachments(commonCreateFeedRequest.attachments(), user, teamspace, AttachmentType.FILE);
			addAttachments(attachmentFiles);
		}
	}

	public void setUser(User user) {
		this.user = user;
		user.getFeeds().add(this);
	}

	public void setTeamspace(Teamspace teamspace) {
		this.teamspace = teamspace;
		teamspace.getFeeds().add(this);
	}

	private void addAttachments(List<Attachment> attachments) {
		List<FeedAttachment> newFeedAttachments = attachments.stream()
			.map(attachment -> FeedAttachment.of(this, attachment))
			.toList();
		this.feedAttachments.addAll(newFeedAttachments);
	}

	private List<Attachment> createAttachments(
		List<CommonCreateFeedRequest.FileDto> fileDtos,
		User user,
		Teamspace teamspace,
		AttachmentType attachmentType
	) {
		return fileDtos.stream()
			.map(fileDto -> Attachment.of(user, teamspace, attachmentType, fileDto))
			.toList();
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
	}
}
