package one.colla.teamspace.application.dto.response;

import one.colla.teamspace.domain.Tag;

public record CreateTagResponse(
	TagDto tag
) {
	public static CreateTagResponse from(Tag tag) {
		TagDto tagDto = TagDto.from(tag);
		return new CreateTagResponse(tagDto);
	}
}
