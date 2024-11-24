package one.colla.feed.factory;

import org.apache.commons.lang3.tuple.Pair;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.normal.application.dto.response.ReadNormalFeedDetails;
import one.colla.feed.normal.domain.NormalFeed;

public class NormalReadFeedDetailsFactory implements ReadFeedDetailsFactory {
	@Override
	public Pair<FeedType, ReadFeedDetails> createReadFeedDetails(Feed feed) {
		if (feed instanceof NormalFeed normalFeed) {
			return Pair.of(FeedType.NORMAL, ReadNormalFeedDetails.from(normalFeed.getContent()));

		} else {
			throw new IllegalArgumentException("Unsupported feed type");
		}
	}
}
