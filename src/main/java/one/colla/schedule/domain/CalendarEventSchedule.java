package one.colla.schedule.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("SCHEDULE")
@Table(name = "calendar_event_schedules")
public class CalendarEventSchedule extends CalendarEvent {

	@Column(name = "location")
	private String location;

}
