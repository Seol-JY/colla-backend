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

}
