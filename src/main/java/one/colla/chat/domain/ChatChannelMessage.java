package one.colla.chat.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import one.colla.chat.application.dto.request.ChatCreateRequest;
import one.colla.chat.domain.vo.ChatChannelMessageContent;
import one.colla.file.domain.Attachment;
import one.colla.file.domain.AttachmentType;
import one.colla.teamspace.domain.Teamspace;
import one.colla.user.domain.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat_channel_messages")
public class ChatChannelMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_channel_id", nullable = false, updatable = false)
	private ChatChannel chatChannel;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private ChatType chatType;

	@Embedded
	private ChatChannelMessageContent content;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "chatChannelMessage", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST,
		orphanRemoval = true)
	private final List<ChatChannelMessageAttachment> chatChannelMessageAttachments = new ArrayList<>();

	private ChatChannelMessage(User user, Teamspace teamspace, ChatChannel chatChannel,
		ChatCreateRequest chatCreateRequest, ChatChannelMessageContent content) {
		this.user = user;
		this.chatChannel = chatChannel;
		this.chatType = chatCreateRequest.chatType();
		this.content = content;

		if (chatCreateRequest.images() != null) {
			List<Attachment> imageFiles =
				createAttachments(chatCreateRequest.images(), user, teamspace, AttachmentType.IMAGE);
			addAttachments(imageFiles);
		}

		if (chatCreateRequest.attachments() != null) {
			List<Attachment> attachmentFiles =
				createAttachments(chatCreateRequest.attachments(), user, teamspace, AttachmentType.FILE);
			addAttachments(attachmentFiles);
		}
	}

	public static ChatChannelMessage of(User user, Teamspace teamspace, ChatChannel chatChannel,
		ChatCreateRequest chatCreateRequest) {
		ChatChannelMessageContent chatChannelMessageContent = null;
		if (chatCreateRequest.content() != null) {
			chatChannelMessageContent = ChatChannelMessageContent.from(
				chatCreateRequest.content());
		}

		return new ChatChannelMessage(user, teamspace, chatChannel, chatCreateRequest, chatChannelMessageContent);
	}

	private void addAttachments(List<Attachment> attachments) {
		List<ChatChannelMessageAttachment> newChannelMessageAttachments = attachments.stream()
			.map(attachment -> ChatChannelMessageAttachment.of(this, attachment))
			.toList();
		this.chatChannelMessageAttachments.addAll(newChannelMessageAttachments);
	}

	private List<Attachment> createAttachments(
		List<ChatCreateRequest.FileDto> fileDtos,
		User user,
		Teamspace teamspace,
		AttachmentType attachmentType
	) {

		return fileDtos.stream()
			.map(fileDto -> Attachment.createChatChannelMessageAttachment(user, teamspace, attachmentType, fileDto))
			.toList();
	}

}
