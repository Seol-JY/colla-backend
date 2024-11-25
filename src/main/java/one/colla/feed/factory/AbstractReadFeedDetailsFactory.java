package one.colla.feed.factory;

import org.apache.commons.lang3.tuple.Pair;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedType;

public abstract class AbstractReadFeedDetailsFactory<T extends Feed> implements ReadFeedDetailsFactory {

	private final FeedType supportedType;

	protected AbstractReadFeedDetailsFactory(FeedType supportedType) {
		this.supportedType = supportedType;
	}

	@Override
	public Pair<FeedType, ReadFeedDetails> createReadFeedDetails(Feed feed) {
		Class<? extends Feed> expectedClass = supportedType.getFeedClass();
		if (!expectedClass.isInstance(feed)) {
			throw new IllegalArgumentException(
				String.format("지원하지 않는 피드 타입. Expected: %s, Got: %s",
					expectedClass.getSimpleName(),
					feed.getClass().getSimpleName()
				)
			);
		}

		@SuppressWarnings("unchecked")
		T typedFeed = (T)feed;
		return Pair.of(supportedType, createDetails(typedFeed));
	}

	@Override
	public FeedType getSupportedFeedType() {
		return supportedType;
	}

	protected abstract ReadFeedDetails createDetails(T feed);
}
