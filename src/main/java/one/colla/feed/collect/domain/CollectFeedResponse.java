package one.colla.feed.collect.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.BaseEntity;
import one.colla.common.domain.CompositeKeyBase;
import one.colla.feed.collect.application.dto.request.UpdateCollectFeedResponseRequest;
import one.colla.user.domain.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "collect_feed_responses")
public class CollectFeedResponse extends BaseEntity {

	@EmbeddedId
	private CollectFeedResponseId collectFeedResponseId = new CollectFeedResponseId();

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@MapsId("collectFeedId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collect_feed_id", nullable = false, updatable = false)
	private CollectFeed collectFeed;

	@Column(name = "title", length = 100)
	private String title;

	@Column(name = "content")
	private String content;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private CollectFeedStatus collectFeedStatus;

	public static CollectFeedResponse createEmptyResponse(User user, CollectFeed collectFeed) {
		return new CollectFeedResponse(user, collectFeed, CollectFeedStatus.PENDING);
	}

	public void changeTitleAndContent(UpdateCollectFeedResponseRequest request) {
		this.title = request.title();
		this.content = request.content();
	}

	public void changeStatus(CollectFeedStatus collectFeedStatus) {
		this.collectFeedStatus = collectFeedStatus;
	}

	private CollectFeedResponse(User user, CollectFeed collectFeed, CollectFeedStatus collectFeedStatus) {
		this.user = user;
		this.collectFeed = collectFeed;
		this.collectFeedStatus = collectFeedStatus;
	}

	public static class CollectFeedResponseId extends CompositeKeyBase {
		@Column(name = "collect_feed_id")
		private Long collectFeedId;

		@Column(name = "user_id")
		private Long userId;
	}
}
