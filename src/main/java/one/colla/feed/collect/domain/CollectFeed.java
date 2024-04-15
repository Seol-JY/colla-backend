package one.colla.feed.collect.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import one.colla.feed.common.domain.Feed;

@Entity
@DiscriminatorValue("COLLECT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "collect_feeds")
public class CollectFeed extends Feed {

	@Column(name = "content")
	private String content;

	@Column(name = "due_at", nullable = false)
	private LocalDateTime dueAt;

	@OneToMany(mappedBy = "collectFeed", fetch = FetchType.LAZY)
	private final List<CollectFeedResponse> collectFeedResponses = new ArrayList<>();

}
