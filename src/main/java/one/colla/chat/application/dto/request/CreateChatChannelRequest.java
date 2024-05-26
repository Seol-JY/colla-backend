package one.colla.chat.application.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CreateChatChannelRequest(
	@NotBlank(message = "채널 이름을 입력해주세요.")
	@Length(min = 1, max = 15, message = "채널 이름은 1자 이상 15자 이하여야 합니다.")
	String chatChannelName
) {
}
