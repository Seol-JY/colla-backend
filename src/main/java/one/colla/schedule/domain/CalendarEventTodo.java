package one.colla.schedule.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("TODO")
@Table(name = "calendar_event_todos")
public class CalendarEventTodo extends CalendarEvent {

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private CalendarEventTodoStatus status;

	@OneToMany(mappedBy = "calendarEventTodo", fetch = FetchType.LAZY)
	private final List<CalendarEventSubtodo> calendarEventSubtodos = new ArrayList<>();

}
