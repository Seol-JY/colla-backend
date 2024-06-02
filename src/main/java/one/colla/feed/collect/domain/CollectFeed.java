package one.colla.feed.collect.domain;

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
import one.colla.feed.collect.application.dto.request.CreateCollectFeedDetails;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.teamspace.domain.Teamspace;
import one.colla.user.domain.User;

@Getter
@Entity
@DiscriminatorValue("COLLECT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "collect_feeds")
public class CollectFeed extends Feed {

	@Column(name = "content")
	private String content;

	@Column(name = "due_at", nullable = false)
	private LocalDateTime dueAt;

	@OneToMany(mappedBy = "collectFeed", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<CollectFeedResponse> collectFeedResponses = new ArrayList<>();

	public static CollectFeed of(
		final User user,
		final Teamspace teamspace,
		final CommonCreateFeedRequest<CreateCollectFeedDetails> createCollectFeedRequest
	) {
		CreateCollectFeedDetails createCollectFeedDetails = createCollectFeedRequest.details();

		return new CollectFeed(
			user,
			teamspace,
			createCollectFeedRequest,
			createCollectFeedDetails.content(),
			createCollectFeedDetails.dueAt()
		);
	}

	public void addResponses(List<CollectFeedResponse> collectFeedResponses) {
		this.collectFeedResponses.addAll(collectFeedResponses);
	}

	private CollectFeed(
		final User user,
		final Teamspace teamspace,
		final CommonCreateFeedRequest<? extends CreateFeedDetails> commonCreateFeedRequest,
		final String content,
		final LocalDateTime dueAt
	) {
		super(user, teamspace, commonCreateFeedRequest);
		this.content = content;
		this.dueAt = dueAt;
	}
}
