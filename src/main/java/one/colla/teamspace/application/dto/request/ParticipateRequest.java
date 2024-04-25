package one.colla.teamspace.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParticipateRequest(
	@NotBlank(message = "초대 코드를 포함해주세요.")
	@Size(min = 10, max = 10, message = "초대 코드는 10자여야 합니다.")
	String inviteCode
) {
}
