package one.colla.common.builder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import one.colla.chat.domain.ChatChannel;
import one.colla.chat.domain.ChatChannelMessage;
import one.colla.chat.domain.UserChatChannel;
import one.colla.feed.normal.domain.NormalFeed;
import one.colla.teamspace.domain.Tag;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

@Component
public class TestFixtureBuilder {

	@Autowired
	private BuilderSupporter bs;

	public User buildUser(final User user) {
		return bs.userRepository().save(user);
	}

	public List<User> buildUsers(final List<User> users) {
		return bs.userRepository().saveAll(users);
	}

	public Teamspace buildTeamspace(final Teamspace teamspace) {
		return bs.teamspaceRepository().save(teamspace);
	}

	public UserTeamspace buildUserTeamspace(final UserTeamspace userTeamspace) {
		return bs.userTeamspaceRepository().save(userTeamspace);
	}

	public List<UserTeamspace> buildUserTeamspaces(final List<UserTeamspace> userTeamspaces) {
		return bs.userTeamspaceRepository().saveAll(userTeamspaces);
	}

	public Tag buildTag(Tag tag) {
		return bs.tagRepository().save(tag);
	}

	public ChatChannel buildChatChannel(ChatChannel chatChannel) {
		return bs.chatChannelRepository().save(chatChannel);
	}

	public List<UserChatChannel> buildUserChatChannel(List<UserChatChannel> userChatChannels) {
		return bs.userChatChannelRepository().saveAll(userChatChannels);
	}

	public ChatChannelMessage buildChatChannelMessage(ChatChannelMessage chatChannelMessage) {
		return bs.chatChannelMessageRepository().save(chatChannelMessage);
	}

	public NormalFeed buildNormalFeed(final NormalFeed feed) {
		return bs.normalFeedRepository().save(feed);
	}
}
