package one.colla.feed.collect.application.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;

public record UpdateCollectFeedResponseRequest(
	@Nullable
	@Size(max = 50, message = "제목은 50자 이하여야 합니다")
	String title,

	@Nullable
	String content
) implements CreateFeedDetails {
}
