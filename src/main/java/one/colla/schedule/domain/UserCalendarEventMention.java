package one.colla.schedule.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

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
@Table(name = "user_calendar_event_mention")
public class UserCalendarEventMention {

	@EmbeddedId
	private UserCalendarEventMentionId userCalendarEventMentionId;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@MapsId("calendarEventId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_event_id", nullable = false, updatable = false)
	private CalendarEvent calendarEvent;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public static class UserCalendarEventMentionId extends CompositeKeyBase {
		@Column(name = "calendar_event_id")
		private Long calendarEventId;

		@Column(name = "user_id")
		private Long userId;
	}
}
