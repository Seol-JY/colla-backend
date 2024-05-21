package one.colla.feed.common.application.dto.response;

import java.util.List;

public record CommonReadFeedListResponse(
	List<CommonReadFeedResponse<ReadFeedDetails>> commonReadFeedResponses
) {
}
