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
@Table(name = "calendar_event_feeds")
public class CalendarEventFeed {

	@EmbeddedId
	private CalendarEventFeedId calendarEventFeedId;

	@MapsId("calendarEventId")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_event_id", nullable = false, updatable = false)
	private CalendarEvent calendarEvent;

	@MapsId("feedId")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id", nullable = false, updatable = false)
	private Feed feed;

	public static class CalendarEventFeedId extends CompositeKeyBase {
		@Column(name = "calendar_event_id")
		private Long calendarEventId;

		@Column(name = "feed_id")
		private Long feedId;
	}
}
