package one.colla.common.application.dto.request;

import java.util.List;

import jakarta.validation.Valid;

public record PreSignedUrlRequest(
	@Valid List<PreSignedUploadDto> preSignedUploadInitiates
) {

}
