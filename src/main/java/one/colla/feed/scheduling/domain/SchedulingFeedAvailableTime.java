package one.colla.feed.scheduling.domain;

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
@Table(name = "scheduling_feed_available_times")
public class SchedulingFeedAvailableTime {

	@EmbeddedId
	private SchedulingFeedAvailableTimeId schedulingFeedAvailableTimeId;

	@MapsId("schedulingFeedTargetDateId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scheduling_feed_target_date_id", nullable = false, updatable = false)
	private SchedulingFeedTargetDate schedulingFeedTargetDate;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@Column(name = "available_time_segment_array")
	private String availableTimeSegmentArray;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public static class SchedulingFeedAvailableTimeId extends CompositeKeyBase {
		@Column(name = "scheduling_feed_target_date_id")
		private Long schedulingFeedTargetDateId;

		@Column(name = "user_id")
		private Long userId;
	}
}
