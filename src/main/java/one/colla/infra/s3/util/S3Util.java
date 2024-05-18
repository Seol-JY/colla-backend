package one.colla.infra.s3.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import one.colla.infra.s3.application.dto.request.DomainType;

@Component
@RequiredArgsConstructor
public class S3Util {

	private static String DELIMITER = "/";

	@Value("${cloud.aws.s3.endpoint}")
	private String endPoint;

	public String createObjectKey(
		final DomainType domainType,
		final Long teamspaceId,
		final String originAttachmentName,
		final Long userId) {
		String dirName = getDirectoryPath(domainType, teamspaceId, userId);
		String fileId = UUID.randomUUID().toString();

		return String.format("%s/%s_%s", dirName, fileId, originAttachmentName);
	}

	public String createAttachmentUrl(final String objectKey) {
		return endPoint + DELIMITER + objectKey;
	}

	private String getDirectoryPath(final DomainType domainType, final Long teamspaceId, final Long userId) {
		return domainType == DomainType.USER
			? String.format("users/%s", userId)
			: String.format("teamspaces/%s/users/%s", teamspaceId, userId);

	}

}
