package one.colla.schedule.domain;

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
import one.colla.user.domain.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_calendar_events")
public class UserCalendarEvent {

	@EmbeddedId
	private UserCalendarEventId userCalendarEventId;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@MapsId("calendarEventId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_event_id", nullable = false, updatable = false)
	private CalendarEvent calendarEvent;

	public static class UserCalendarEventId extends CompositeKeyBase {
		@Column(name = "calendar_event_id")
		private Long calendarEventId;

		@Column(name = "user_id")
		private Long userId;
	}
}
