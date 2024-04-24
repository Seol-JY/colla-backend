package one.colla.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerificationCheckRequest(
	@NotBlank(message = "이메일을 입력해주세요.")
	@Email(message = "이메일 형식이 아닙니다.")
	String email,

	@NotBlank(message = "인증번호를 입력해주세요.")
	@Size(min = 7, max = 7, message = "인증번호는 7자리여야 합니다.")
	String verifyCode
) {
}
