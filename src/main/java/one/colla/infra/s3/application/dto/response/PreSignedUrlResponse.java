package one.colla.infra.s3.application.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record PreSignedUrlResponse(
	List<FileUploadUrlsDto> fileUploadUrlsDtos
) {
	public static PreSignedUrlResponse from(List<FileUploadUrlsDto> fileUploadUrlsDtos) {
		return new PreSignedUrlResponse(fileUploadUrlsDtos);
	}
}
