package one.colla.feed.normal.application.dto.response;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;

public record ReadNormalFeedDetails(
	String content
) implements ReadFeedDetails {
	public static ReadNormalFeedDetails from(String content) {
		return new ReadNormalFeedDetails(content);
	}
}
