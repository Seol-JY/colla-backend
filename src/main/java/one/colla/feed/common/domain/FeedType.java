package one.colla.feed.common.domain;

public enum FeedType {
	NORMAL(Values.NORMAL),
	VOTE(Values.VOTE),
	SCHEDULING(Values.SCHEDULING),
	COLLECT(Values.COLLECT);

	private final String value;

	FeedType(String val) {
		this.value = val;
		if (!this.name().equals(val)) {
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
