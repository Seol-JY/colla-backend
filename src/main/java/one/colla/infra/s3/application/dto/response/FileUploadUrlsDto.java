package one.colla.infra.s3.application.dto.response;

import java.net.URL;

import lombok.Builder;

@Builder
public record FileUploadUrlsDto(URL presignedUrl, String attachmentUrl) {
}
