package one.colla.feed.scheduling.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.scheduling.application.dto.request.CreateSchedulingFeedDetails;
import one.colla.teamspace.domain.Teamspace;
import one.colla.user.domain.User;

@Getter
@Entity
@DiscriminatorValue("SCHEDULING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "scheduling_feeds")
public class SchedulingFeed extends Feed {

	@Column(name = "min_time_segment", nullable = false)
	private Byte minTimeSegment;

	@Column(name = "max_time_segment", nullable = false)
	private Byte maxTimeSegment;

	@Column(name = "num_of_participants", nullable = false)
	private Byte numOfParticipants;

	@Column(name = "due_at", nullable = false)
	private LocalDateTime dueAt;

	@OneToMany(mappedBy = "schedulingFeed", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<SchedulingFeedTargetDate> schedulingFeedTargetDates = new ArrayList<>();

	public static SchedulingFeed of(
		final User user,
		final Teamspace teamspace,
		final CommonCreateFeedRequest<CreateSchedulingFeedDetails> createSchedulingFeedRequest
	) {
		CreateSchedulingFeedDetails createSchedulingFeedDetails = createSchedulingFeedRequest.details();

		return new SchedulingFeed(
			user,
			teamspace,
			createSchedulingFeedRequest,
			createSchedulingFeedDetails.minTimeSegment(),
			createSchedulingFeedDetails.maxTimeSegment(),
			createSchedulingFeedDetails.dueAt()
		);
	}

	public void addTargetDates(List<SchedulingFeedTargetDate> schedulingFeedTargetDates) {
		this.schedulingFeedTargetDates.addAll(schedulingFeedTargetDates);
	}

	public void increaseNumOfParticipants() {
		this.numOfParticipants++;
	}

	public void decreaseNumOfParticipants() {
		this.numOfParticipants--;
	}

	private SchedulingFeed(
		final User user,
		final Teamspace teamspace,
		final CommonCreateFeedRequest<? extends CreateFeedDetails> commonCreateFeedRequest,
		final Byte minTimeSegment,
		final Byte maxTimeSegment,
		final LocalDateTime dueAt
	) {
		super(user, teamspace, commonCreateFeedRequest);
		this.dueAt = dueAt;
		this.minTimeSegment = minTimeSegment;
		this.maxTimeSegment = maxTimeSegment;
		this.numOfParticipants = 0;
	}
}
