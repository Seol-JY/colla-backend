package one.colla.chat.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatChannelRepository extends JpaRepository<UserChatChannel, Long> {
	Optional<UserChatChannel> findByUserIdAndChatChannelId(Long userId, Long id);
}
