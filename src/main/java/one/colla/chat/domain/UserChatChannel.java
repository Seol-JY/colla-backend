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
import one.colla.user.domain.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_chat_channels")
public class UserChatChannel extends BaseEntity {

	@EmbeddedId
	private UserChatChannelId userChatChannelId = new UserChatChannelId();

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@MapsId("chatChannelId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_channel_id", nullable = false, updatable = false)
	private ChatChannel chatChannel;

	@Column(name = "last_read_message_id")
	private Long lastReadMessageId;

	private UserChatChannel(User user, ChatChannel chatChannel) {
		this.user = user;
		this.chatChannel = chatChannel;
	}

	public static UserChatChannel of(User user, ChatChannel chatChannel) {
		return new UserChatChannel(user, chatChannel);
	}

	public static class UserChatChannelId extends CompositeKeyBase {
		@Column(name = "user_id")
		private Long userId;

		@Column(name = "chat_channel_id")
		private Long chatChannelId;
	}

	public void updateLastReadMessageId(Long lastReadMessageId) {
		this.lastReadMessageId = lastReadMessageId;
	}
}
