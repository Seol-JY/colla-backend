package one.colla.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.BaseEntity;
import one.colla.common.domain.CompositeKeyBase;
import one.colla.file.domain.Attachment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_chat_channel")
public class ChatChannelMessageAttachment extends BaseEntity {

	@EmbeddedId
	private ChatChannelMessageAttachmentId chatChannelMessageAttachmentId;

	@MapsId("chatChannelMessageId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_channel_message_id", nullable = false, updatable = false)
	private ChatChannelMessage chatChannelMessage;

	@MapsId("attachmentId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attachment_id", nullable = false, updatable = false)
	private Attachment attachment;

	public static class ChatChannelMessageAttachmentId extends CompositeKeyBase {
		@Column(name = "chat_channel_message_id")
		private Long chatChannelMessageId;

		@Column(name = "attachment_id")
		private Long attachmentId;
	}
}
