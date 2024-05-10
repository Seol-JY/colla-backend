package one.colla.user.application.dto.request;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import jakarta.annotation.Nullable;
import one.colla.user.domain.CommentNotification;

public record UpdateUserSettingRequest(
	@Nullable
	@URL(message = "프로필 이미지 URL 형식이 올바르지 않습니다.")
	String profileImageUrl,

	@Nullable
	@Length(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하이어야 합니다.")
	String username,

	@Nullable
	Boolean emailSubscription,

	@Nullable
	CommentNotification commentNotification
) {
}
