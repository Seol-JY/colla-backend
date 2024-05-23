package one.colla.file.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.chat.domain.ChatChannelMessageAttachment;
import one.colla.common.domain.BaseEntity;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.file.domain.vo.AttachmentName;
import one.colla.file.domain.vo.FileUrl;
import one.colla.teamspace.domain.Teamspace;
import one.colla.user.domain.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attachments")
public class Attachment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private AttachmentName attachmentName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teamspace_id", nullable = false, updatable = false)
	private Teamspace teamspace;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private AttachmentType attachmentType;

	@Column(name = "size", nullable = false)
	private Long size;

	@Column(name = "attach_type", nullable = false)
	private String attachType;

	@Embedded
	private FileUrl fileUrl;

	@OneToMany(mappedBy = "attachment", fetch = FetchType.LAZY)
	private final List<ChatChannelMessageAttachment> chatChannelMessageAttachments = new ArrayList<>();

	public Attachment(
		AttachmentName attachmentName,
		User user,
		Teamspace teamspace,
		AttachmentType attachmentType,
		Long size,
		String attachType,
		FileUrl fileUrl
	) {
		this.attachmentName = attachmentName;
		this.user = user;
		this.teamspace = teamspace;
		this.attachmentType = attachmentType;
		this.size = size;
		this.attachType = attachType;
		this.fileUrl = fileUrl;
	}

	public static Attachment of(
		User user,
		Teamspace teamspace,
		AttachmentType attachmentType,
		CommonCreateFeedRequest.FileDto fileDto
	) {
		AttachmentName attachmentName = AttachmentName.from(fileDto.name());
		FileUrl fileUrl = FileUrl.from(fileDto.fileUrl());
		String attachType = getFileExtensionByUrl(fileUrl.getValue());

		return new Attachment(
			attachmentName,
			user,
			teamspace,
			attachmentType,
			fileDto.size(),
			attachType,
			fileUrl
		);
	}

	private static String getFileExtensionByUrl(String url) {
		if (url == null || url.isEmpty()) {
			return "";
		}

		int lastDotIndex = url.lastIndexOf('.');
		int lastSlashIndex = url.lastIndexOf('/');

		if (lastDotIndex == -1 || lastDotIndex < lastSlashIndex || lastDotIndex == url.length() - 1) {
			return "";
		}

		return url.substring(lastDotIndex + 1);
	}
}
