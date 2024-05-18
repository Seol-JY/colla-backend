package one.colla.infra.s3.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import one.colla.common.presentation.ApiResponse;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.infra.s3.application.FileService;
import one.colla.infra.s3.application.dto.request.PreSignedUrlRequest;
import one.colla.infra.s3.application.dto.response.PreSignedUrlResponse;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class S3Controller {

	private final FileService s3Service;

	@PostMapping("/presigned")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<PreSignedUrlResponse>> getPresignedUrls(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody PreSignedUrlRequest request) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccessResponse(s3Service.getPresignedUrl(request, userDetails)));
	}
}
