package one.colla.teamspace.application.dto.response;

import jakarta.annotation.Nullable;
import one.colla.teamspace.domain.Tag;

public record TagDto(
	Long id,
	String name
) {
	public static TagDto from(@Nullable Tag tag) {
		return tag != null ? new TagDto(tag.getId(), tag.getName()) : null;
	}
}
