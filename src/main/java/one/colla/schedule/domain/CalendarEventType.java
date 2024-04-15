package one.colla.schedule.domain;

public enum CalendarEventType {
	SCHEDULE(Values.SCHEDULE),
	TODO(Values.TODO),
	FEED(Values.FEED);

	private final String value;

	CalendarEventType(String val) {
		this.value = val;
		if (!this.name().equals(val)) {
			throw new IllegalArgumentException("Incorrect use of DType");
		}
	}

	public static class Values {
		public static final String SCHEDULE = "SCHEDULE";
		public static final String TODO = "TODO";
		public static final String FEED = "FEED";
	}
}
