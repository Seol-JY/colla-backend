package one.colla.file.application.dto.response;

import java.util.List;

public record StorageResponse(
	Long totalStorageCapacity,
	List<AttachmentInfoDto> attachments
) {
	public static StorageResponse of(Long totalStorageCapacity, List<AttachmentInfoDto> attachments) {
		return new StorageResponse(totalStorageCapacity, attachments);
	}
}
