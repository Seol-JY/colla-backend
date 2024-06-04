package one.colla.file.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

	@Query("SELECT a FROM Attachment a "
		+ "WHERE a.teamspace.id = :teamspaceId "
		+ "AND (:type IS NULL OR a.attachmentType = :type) "
		+ "AND (:attachType IS NULL OR a.attachType = :attachType) "
		+ "AND (:username IS NULL OR a.user.username.value = :username)")
	List<Attachment> findAttachments(
		@Param("teamspaceId") Long teamspaceId,
		@Param("type") AttachmentType type,
		@Param("attachType") String attachType,
		@Param("username") String username);

	@Query("SELECT SUM(a.size) FROM Attachment a WHERE a.teamspace.id = :teamspaceId")
	Long calculateTotalStorageCapacity(@Param("teamspaceId") Long teamspaceId);
}
