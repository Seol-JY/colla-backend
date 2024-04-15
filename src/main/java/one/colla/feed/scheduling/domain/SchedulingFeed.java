package one.colla.feed.scheduling.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import one.colla.feed.common.domain.Feed;

@Entity
@DiscriminatorValue("SCHEDULING")
@Table(name = "scheduling_feeds")
public class SchedulingFeed extends Feed {

	@Column(name = "min_time_segment", nullable = false)
	private int minTimeSegment;

	@Column(name = "max_time_segment", nullable = false)
	private int maxTimeSegment;

	@Column(name = "num_of_participants", nullable = false)
	private int numOfParticipants;

	@Column(name = "due_at", nullable = false)
	private LocalDateTime dueAt;

	@OneToMany(mappedBy = "schedulingFeed", fetch = FetchType.LAZY)
	private final List<SchedulingFeedTargetDate> schedulingFeedTargetDates = new ArrayList<>();

}
