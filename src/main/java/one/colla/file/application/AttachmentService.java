package one.colla.file.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.file.application.dto.response.AttachmentAuthorDto;
import one.colla.file.application.dto.response.AttachmentInfoDto;
import one.colla.file.application.dto.response.StorageResponse;
import one.colla.file.domain.Attachment;
import one.colla.file.domain.AttachmentRepository;
import one.colla.file.domain.AttachmentType;
import one.colla.teamspace.application.TeamspaceService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {
	private final AttachmentRepository attachmentRepository;
	private final TeamspaceService teamspaceService;

	@Transactional(readOnly = true)
	public StorageResponse getAttachments(
		CustomUserDetails userDetails, Long teamspaceId, AttachmentType type,
		String attachType, String username) {

		validateParticipationInTeamspace(teamspaceId, userDetails);

		List<Attachment> attachments = attachmentRepository.findAttachments(teamspaceId, type,
			attachType, username);

		List<AttachmentInfoDto> attachmentInfoDtos = attachments.stream()
			.map(at -> AttachmentInfoDto.of(at, AttachmentAuthorDto.from(at.getUser())))
			.toList();

		Long totalStorageCapacity = attachmentRepository.calculateTotalStorageCapacity(teamspaceId);

		return StorageResponse.of(totalStorageCapacity, attachmentInfoDtos);
	}

	private void validateParticipationInTeamspace(Long teamspaceId, CustomUserDetails customUserDetails) {
		teamspaceService.getUserTeamspace(customUserDetails, teamspaceId);
	}
}
