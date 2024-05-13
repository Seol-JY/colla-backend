package one.colla.common.application.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record PreSignedUrlResponse(
	List<AttachmentResponse> attachmentResponses
) {
	public static PreSignedUrlResponse from(List<AttachmentResponse> attachmentResponses) {
		return new PreSignedUrlResponse(attachmentResponses);
	}
}
