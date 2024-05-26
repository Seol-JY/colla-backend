package one.colla.chat.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
import one.colla.chat.domain.vo.ChatChannelMessageContent;
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

	@OneToMany(mappedBy = "chatChannelMessage", fetch = FetchType.LAZY)
	private final List<ChatChannelMessageAttachment> chatChannelMessageAttachments = new ArrayList<>();

	private ChatChannelMessage(User user, ChatChannel chatChannel, ChatType chatType,
		ChatChannelMessageContent content) {
		this.user = user;
		this.chatChannel = chatChannel;
		this.chatType = chatType;
		this.content = content;
	}

	public static ChatChannelMessage of(User user, ChatChannel chatChannel, ChatType chatType, String content) {
		ChatChannelMessageContent chatChannelMessageContent = ChatChannelMessageContent.from(content);
		return new ChatChannelMessage(user, chatChannel, chatType, chatChannelMessageContent);
	}

}
