package one.colla.teamspace.application.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UpdateTeamspaceSettingsRequest(
	@Nullable
	@URL(message = "프로필 이미지 URL 형식이 올바르지 않습니다.")
	String profileImageUrl,

	@Nullable
	@Length(min = 2, max = 20, message = "팀스페이스 길이는 2자 이상 20자 이하여야 합니다.")
	String name,

	@Valid
	@Nullable
	List<UserUpdateInfo> users
) {
	public record UserUpdateInfo(
		@NotNull(message = "userId는 null 일 수 없습니다.")
		Long id,
		@NotNull(message = "tagId는 null 일 수 없습니다.")
		Long tagId
	) {
	}
}
