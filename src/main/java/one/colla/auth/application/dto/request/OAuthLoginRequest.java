package one.colla.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OAuthLoginRequest(
	@NotBlank(message = "Authorization code를 입력해주세요.")
	String code
) {
}
