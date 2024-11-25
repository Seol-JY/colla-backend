package one.colla.feed.factory;

import org.springframework.stereotype.Component;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;
import one.colla.feed.common.domain.FeedType;
import one.colla.feed.normal.application.dto.response.ReadNormalFeedDetails;
import one.colla.feed.normal.domain.NormalFeed;

@Component
public class NormalReadFeedDetailsFactory extends AbstractReadFeedDetailsFactory<NormalFeed> {
	public NormalReadFeedDetailsFactory() {
		super(FeedType.NORMAL);
	}

	@Override
	protected ReadFeedDetails createDetails(NormalFeed feed) {
		return ReadNormalFeedDetails.from(feed.getContent());
	}
}
