package one.colla.feed.collect.application.dto.response;

import one.colla.feed.common.application.dto.response.ReadFeedDetails;

public record ReadCollectFeedDetails(
	String content
	// TODO: 추가 필요
) implements ReadFeedDetails {
	public static ReadCollectFeedDetails from(String content) {
		return new ReadCollectFeedDetails(content);
	}
}
