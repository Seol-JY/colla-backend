package one.colla.common.application.dto.response;

import java.net.URL;

import lombok.Builder;

@Builder
public record AttachmentResponse(URL presignedUrl, String attachmentUrl) {
}
