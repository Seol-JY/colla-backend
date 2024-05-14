package one.colla.common.application;

import java.net.URL;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import lombok.RequiredArgsConstructor;
import one.colla.common.application.dto.request.DomainType;
import one.colla.common.application.dto.request.FileUploadDto;
import one.colla.common.application.dto.request.PreSignedUrlRequest;
import one.colla.common.application.dto.response.FileUploadUrlsDto;
import one.colla.common.application.dto.response.PreSignedUrlResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.common.util.S3Util;
import one.colla.teamspace.application.TeamspaceService;

@Service
@RequiredArgsConstructor
public class FileService {

	private static final long PRESIGNED_URL_EXPIRATION_TIME = 1000 * 60 * 2;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final S3Util s3Util;
	private final AmazonS3 amazonS3;
	private final TeamspaceService teamspaceService;

	@Transactional(readOnly = true)
	public PreSignedUrlResponse getPresignedUrl(PreSignedUrlRequest request,
		CustomUserDetails userDetails) {

		List<FileUploadUrlsDto> fileUploadUrlsDtos = request.fileUploadDtos().stream()
			.map(ui -> {
				validateParticipationIfTeamspaceType(ui, userDetails);

				String objectKey = s3Util.createObjectKey(
					ui.domainType(),
					ui.teamspaceId(),
					ui.originalAttachmentName(),
					userDetails.getUserId()
				);
				URL presignedUrl = generatePresignedUrl(bucket, objectKey);
				String attachmentUrl = s3Util.createAttachmentUrl(objectKey);
				return new FileUploadUrlsDto(presignedUrl, attachmentUrl);
			})
			.toList();

		return new PreSignedUrlResponse(fileUploadUrlsDtos);
	}

	private URL generatePresignedUrl(String bucket, String objectKey) {
		GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(bucket, objectKey)
			.withMethod(HttpMethod.PUT)
			.withExpiration(getPresignedUrlExpiration());

		presignedUrlRequest.addRequestParameter(
			Headers.S3_CANNED_ACL,
			CannedAccessControlList.PublicRead.toString()
		);

		return amazonS3.generatePresignedUrl(presignedUrlRequest);
	}

	private void validateParticipationIfTeamspaceType(FileUploadDto ui, CustomUserDetails userDetails) {
		if (ui.domainType() == DomainType.TEAMSPACE) {
			teamspaceService.getUserTeamspace(userDetails, ui.teamspaceId());
		}
	}

	private Date getPresignedUrlExpiration() {
		Date expiration = new Date();
		expiration.setTime(expiration.getTime() + PRESIGNED_URL_EXPIRATION_TIME);
		return expiration;
	}

}
