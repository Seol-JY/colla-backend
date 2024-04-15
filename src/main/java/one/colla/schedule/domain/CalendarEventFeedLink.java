package one.colla.schedule.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.CompositeKeyBase;
import one.colla.feed.common.domain.Feed;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "calendar_event_feed_links")
public class CalendarEventFeedLink {

	@EmbeddedId
	private CalendarEventFeedLinkId calendarEventFeedLinkId;

	@MapsId("calendarEventFeedId")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_event_feed_id", nullable = false, updatable = false)
	private CalendarEventFeed calendarEventFeed;

	@MapsId("feedId")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id", nullable = false, updatable = false)
	private Feed feed;

	public static class CalendarEventFeedLinkId extends CompositeKeyBase {
		@Column(name = "calendar_event_feed_id")
		private Long calendarEventFeedId;

		@Column(name = "feed_id")
		private Long feedId;
	}
}
