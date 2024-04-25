package one.colla.auth.application.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
	@NotBlank(message = "닉네임을 입력해주세요.")
	@Length(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하이어야 합니다.")
	String username,

	@NotBlank(message = "이메일을 입력해주세요.")
	@Email(message = "이메일 형식이 아닙니다.")
	String email,

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Length(min = 8, max = 255, message = "비밀번호는 8자 이상 255자 이하이어야 합니다.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "비밀번호는 영문자와 숫자를 포함해야 합니다.")
	String password,

	@NotBlank(message = "인증번호를 입력해주세요.")
	@Length(min = 7, max = 7, message = "인증번호는 7자리여야 합니다.")
	String verifyCode
) {
}
