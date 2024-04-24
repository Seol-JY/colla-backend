package one.colla.teamspace.application.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CreateTeamspaceRequest(
	@NotBlank(message = "팀스페이스 이름을 입력해주세요.")
	@Length(min = 2, max = 50, message = "팀스페이스 길이는 2자 이상 50자 이하여야 합니다.")
	String teamspaceName
) {
}
