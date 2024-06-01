package one.colla.feed.common.application.dto.response;

import java.util.List;

public record CommonReadFeedListResponse(
	List<CommonReadFeedResponse<ReadFeedDetails>> feeds
) {
	public static CommonReadFeedListResponse from(
		final List<CommonReadFeedResponse<ReadFeedDetails>> commonReadFeedResponses
	) {
		return new CommonReadFeedListResponse(commonReadFeedResponses);
	}
}
