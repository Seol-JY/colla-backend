package one.colla.infra.s3.application.dto.request;

import java.util.List;

import jakarta.validation.Valid;

public record PreSignedUrlRequest(
	@Valid List<FileUploadDto> fileUploadDtos
) {

}
