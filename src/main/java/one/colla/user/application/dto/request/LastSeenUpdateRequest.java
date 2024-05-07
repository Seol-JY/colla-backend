package one.colla.user.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record LastSeenUpdateRequest(
	@NotNull
	Long teamspaceId
) {
}
