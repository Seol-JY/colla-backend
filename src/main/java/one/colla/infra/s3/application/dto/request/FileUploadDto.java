package one.colla.infra.s3.application.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record FileUploadDto(
	@NotNull(message = "도메인 타입을 입력해주세요.")
	DomainType domainType,

	Long teamspaceId,

	@NotBlank(message = "파일 이름을 입력해주세요.")
	@Pattern(regexp = ".*\\.[^\\.]+$", message = "파일 이름에는 확장자가 포함되어야 합니다.")
	String originalAttachmentName
) {

	@AssertTrue(message = "도메인 타입이 팀스페이스일 경우 팀스페이스 id를 입력하셔야 합니다.")
	boolean isTeamspaceIdValid() {
		return domainType != DomainType.TEAMSPACE || teamspaceId != null;
	}

	@AssertTrue(message = "도메인 타입이 유저일 경우 팀스페이스 id를 입력하시면 안됩니다.")
	boolean isTeamspaceIdNullValid() {
		return domainType != DomainType.USER || teamspaceId == null;
	}
}
