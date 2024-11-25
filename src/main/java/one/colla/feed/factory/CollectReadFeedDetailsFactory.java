package one.colla.feed.factory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import one.colla.feed.collect.application.dto.response.ReadCollectFeedDetails;
import one.colla.feed.collect.domain.CollectFeed;
import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.common.util.DateTimeUtil;

@Component
public class CollectReadFeedDetailsFactory extends AbstractReadFeedDetailsFactory<CollectFeed> {

	public CollectReadFeedDetailsFactory() {
		super(FeedType.COLLECT);
	}

	@Override
	protected ReadFeedDetails createDetails(CollectFeed collectFeed) {
		LocalDateTime dueAt = collectFeed.getDueAt().equals(DateTimeUtil.INFINITY) ? null : collectFeed.getDueAt();
		boolean isClosed = DateTimeUtil.isDeadlinePassed(collectFeed.getDueAt());

		List<ReadCollectFeedDetails.ReadCollectFeedResponse> responses
			= collectFeed.getCollectFeedResponses()
			.stream()
			.map(cfr -> {
				ReadCollectFeedDetails.CollectFeedResponseAuthor author
					= ReadCollectFeedDetails.CollectFeedResponseAuthor.from(cfr.getUser());
				return ReadCollectFeedDetails.ReadCollectFeedResponse.of(cfr, author);
			})
			.toList();

		return ReadCollectFeedDetails.of(collectFeed.getContent(), dueAt, isClosed, responses);
	}
}

