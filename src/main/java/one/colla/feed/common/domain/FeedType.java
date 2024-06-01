package one.colla.feed.common.domain;

import lombok.Getter;
import one.colla.feed.collect.domain.CollectFeed;
import one.colla.feed.normal.domain.NormalFeed;
import one.colla.feed.scheduling.domain.SchedulingFeed;
import one.colla.feed.vote.domain.VoteFeed;

public enum FeedType {
	NORMAL(Values.NORMAL, NormalFeed.class),
	VOTE(Values.VOTE, VoteFeed.class),
	SCHEDULING(Values.SCHEDULING, SchedulingFeed.class),
	COLLECT(Values.COLLECT, CollectFeed.class);

	private final String value;
	@Getter
	private final Class<? extends Feed> feedClass;

	FeedType(String value, Class<? extends Feed> feedClass) {
		this.value = value;
		this.feedClass = feedClass;
		if (!this.name().equals(value)) {
			throw new IllegalArgumentException("Incorrect use of DType");
		}
	}

	public static class Values {
		public static final String NORMAL = "NORMAL";
		public static final String VOTE = "VOTE";
		public static final String SCHEDULING = "SCHEDULING";
		public static final String COLLECT = "COLLECT";
	}
}
