package one.colla.feed.scheduling.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.user.domain.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "scheduling_feed_target_dates")
public class SchedulingFeedTargetDate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scheduling_feed_id", nullable = false, updatable = false)
	private SchedulingFeed schedulingFeed;

	@Column(name = "target_date", nullable = false)
	private LocalDate targetDate;

	@OneToMany(mappedBy = "schedulingFeedTargetDate", fetch = FetchType.LAZY,
		cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<SchedulingFeedAvailableTime> schedulingFeedAvailableTimes = new ArrayList<>();

	public static SchedulingFeedTargetDate of(SchedulingFeed schedulingFeed, LocalDate targetDate) {
		return new SchedulingFeedTargetDate(schedulingFeed, targetDate);
	}

	public Optional<SchedulingFeedAvailableTime> getSchedulingFeedAvailableTimeByUser(User user) {
		for (SchedulingFeedAvailableTime availableTime : this.schedulingFeedAvailableTimes) {
			if (availableTime.getUser().equals(user)) {
				return Optional.of(availableTime);
			}
		}

		return Optional.empty();
	}

	public boolean removeSchedulingFeedAvailableTimeByUser(User user) {
		for (SchedulingFeedAvailableTime availableTime : this.schedulingFeedAvailableTimes) {
			if (availableTime.getUser().equals(user)) {
				this.schedulingFeedAvailableTimes.remove(availableTime);
				return true;
			}
		}

		return false;
	}

	public void addSchedulingFeedAvailableTime(SchedulingFeedAvailableTime schedulingFeedAvailableTime) {
		this.schedulingFeedAvailableTimes.add(schedulingFeedAvailableTime);
	}

	private SchedulingFeedTargetDate(SchedulingFeed schedulingFeed, LocalDate targetDate) {
		this.schedulingFeed = schedulingFeed;
		this.targetDate = targetDate;
	}
}
