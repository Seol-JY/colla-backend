package one.colla.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatChannelMessageRepository extends JpaRepository<ChatChannelMessage, Long> {
}
