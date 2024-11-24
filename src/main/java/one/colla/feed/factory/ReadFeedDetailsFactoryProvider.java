package one.colla.feed.factory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import one.colla.feed.collect.domain.CollectFeed;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.normal.domain.NormalFeed;
import one.colla.feed.scheduling.domain.SchedulingFeed;

@Component
public class ReadFeedDetailsFactoryProvider {
	private final Map<Class<? extends Feed>, ReadFeedDetailsFactory> factories = new HashMap<>();

	public ReadFeedDetailsFactoryProvider() {
		factories.put(NormalFeed.class, new NormalReadFeedDetailsFactory());
		factories.put(CollectFeed.class, new CollectReadFeedDetailsFactory());
		factories.put(SchedulingFeed.class, new SchedulingReadFeedDetailsFactory());
		// 다른 피드 타입의 팩토리도 여기에 추가
	}

	public ReadFeedDetailsFactory getFactory(Feed feed) {
		ReadFeedDetailsFactory factory = factories.get(feed.getClass());

		if (factory == null) {
			throw new IllegalArgumentException("No factory for feed type: " + feed.getClass().getName());
		}
		return factory;
	}
}
