package one.colla.teamspace.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendMailInviteCodeRequest(
	@NotBlank(message = "메일을 입력해주세요.")
	@Email(message = "메일 형식이 아닙니다.")
	String email
) {
}
