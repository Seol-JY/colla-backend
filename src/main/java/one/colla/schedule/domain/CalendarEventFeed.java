package one.colla.schedule.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@DiscriminatorValue("FEED")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "calendar_event_feeds")
public class CalendarEventFeed extends CalendarEvent {

	@OneToOne(mappedBy = "calendarEventFeed", cascade = CascadeType.ALL)
	private CalendarEventFeedLink calendarEventFeedLink;

}
