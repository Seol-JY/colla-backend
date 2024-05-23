package one.colla.chat.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
import one.colla.chat.domain.vo.ChatChannelName;
import one.colla.common.domain.BaseEntity;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_channels")
public class ChatChannel extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teamspace_id", nullable = false, updatable = false)
	private Teamspace teamspace;

	@Embedded
	private ChatChannelName chatChannelName;

	@Column(name = "last_chat_id")
	private Long lastChatId;

	@OneToMany(mappedBy = "chatChannel", fetch = FetchType.LAZY)
	private final List<UserChatChannel> userChatChannels = new ArrayList<>();

	@OneToMany(mappedBy = "chatChannel", fetch = FetchType.LAZY)
	private final List<ChatChannelMessage> chatChannelMessages = new ArrayList<>();

	private ChatChannel(Teamspace teamspace, ChatChannelName name) {
		this.teamspace = teamspace;
		this.chatChannelName = name;
	}

	public static ChatChannel of(final Teamspace teamspace, final String channelName) {
		ChatChannelName name = ChatChannelName.from(channelName);
		return new ChatChannel(teamspace, name);
	}

	public List<UserChatChannel> participateAllTeamspaceUser(List<UserTeamspace> userTeamspaces) {
		List<UserChatChannel> userChatChannelList = userTeamspaces.stream()
			.map(ut -> UserChatChannel.of(ut.getUser(), this))
			.toList();
		this.userChatChannels.addAll(userChatChannelList);
		return userChatChannelList;
	}
}
