package one.colla.feed.normal.application.dto.request;

import one.colla.feed.common.application.dto.request.CreateFeedDetails;

public record CreateNormalFeedDetails(
	String content
) implements CreateFeedDetails {
}
