package one.colla.feed.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedType;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;

@Component
public class ReadFeedDetailsFactoryProvider {
	private final Map<FeedType, ReadFeedDetailsFactory> factoriesByType;

	public ReadFeedDetailsFactoryProvider(List<ReadFeedDetailsFactory> factories) {
		this.factoriesByType = factories.stream()
			.collect(Collectors.toUnmodifiableMap(
				ReadFeedDetailsFactory::getSupportedFeedType,
				factory -> factory
			));
	}

	public ReadFeedDetailsFactory getFactory(Feed feed) {
		FeedType feedType = Arrays.stream(FeedType.values())
			.filter(type -> type.getFeedClass().isInstance(feed))
			.findFirst()
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_FEED));

		return getFactory(feedType);
	}

	public ReadFeedDetailsFactory getFactory(FeedType feedType) {
		ReadFeedDetailsFactory factory = factoriesByType.get(feedType);
		if (factory == null) {
			throw new CommonException(ExceptionCode.NOT_FOUND_FEED);
		}
		return factory;
	}
}
