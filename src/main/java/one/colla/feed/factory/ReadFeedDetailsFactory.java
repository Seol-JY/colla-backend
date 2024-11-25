package one.colla.feed.factory;

import org.apache.commons.lang3.tuple.Pair;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedType;

public interface ReadFeedDetailsFactory {
	FeedType getSupportedFeedType();

	Pair<FeedType, ReadFeedDetails> createReadFeedDetails(Feed feed);
}
