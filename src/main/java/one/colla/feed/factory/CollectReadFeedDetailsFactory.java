package one.colla.feed.factory;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import one.colla.feed.collect.application.dto.response.ReadCollectFeedDetails;
import one.colla.feed.collect.domain.CollectFeed;
import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.common.util.DateTimeUtil;

public class CollectReadFeedDetailsFactory implements ReadFeedDetailsFactory {
	@Override
	public Pair<FeedType, ReadFeedDetails> createReadFeedDetails(Feed feed) {
		if (feed instanceof CollectFeed collectFeed) {

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

			ReadCollectFeedDetails details
				= ReadCollectFeedDetails.of(collectFeed.getContent(), dueAt, isClosed, responses);

			return Pair.of(FeedType.COLLECT, details);

		} else {
			throw new IllegalArgumentException("Unsupported feed type");
		}
	}
}
