package one.colla.feed.common.domain;

import jakarta.persistence.CascadeType;
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
import one.colla.common.domain.CompositeKeyBase;
import one.colla.file.domain.Attachment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feed_attachments")
public class FeedAttachment {

	@EmbeddedId
	private FeedAttachmentId feedAttachmentId = new FeedAttachmentId();

	@MapsId("feedId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id", nullable = false, updatable = false)
	private Feed feed;

	@MapsId("attachmentId")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_id", nullable = false, updatable = false)
	private Attachment attachment;

	private FeedAttachment(Feed feed, Attachment attachment) {
		this.feed = feed;
		this.attachment = attachment;
	}

	public static FeedAttachment of(Feed feed, Attachment attachment) {
		return new FeedAttachment(feed, attachment);
	}

	public static class FeedAttachmentId extends CompositeKeyBase {
		@Column(name = "feed_id")
		private Long feedId;

		@Column(name = "attachment_id")
		private Long attachmentId;
	}
}
