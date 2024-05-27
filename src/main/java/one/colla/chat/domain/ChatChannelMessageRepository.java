package one.colla.chat.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import io.micrometer.common.lang.Nullable;

public interface ChatChannelMessageRepository extends JpaRepository<ChatChannelMessage, Long> {

	/**
	 * 주어진 채팅 채널과 기준에 따라 채팅 메시지를 찾습니다.
	 * 결과는 생성 날짜 기준 내림차순으로 정렬됩니다.
	 *
	 * @param chatChannel 채팅 메시지를 필터링할 채팅 채널
	 * @param before 선택적 채팅 메시지 ID로, 이 채팅 메시지의 생성 날짜 이전에 생성된 채팅 메시지들을 필터링합니다
	 * @param pageable 페이징 정보
	 * @return 주어진 기준에 맞는 채팅 메시지 목록
	 */
	@Query("SELECT msg FROM ChatChannelMessage msg WHERE msg.chatChannel = :chatChannel "
		+ "AND (:before IS NULL "
		+ "OR msg.createdAt < (SELECT bmsg.createdAt FROM ChatChannelMessage bmsg WHERE bmsg.id = :before)) "
		+ "ORDER BY msg.createdAt DESC")
	List<ChatChannelMessage> findChatChannelMessageByChatChannelAndCriteria(
		@Param("chatChannel") ChatChannel chatChannel,
		@Param("before") @Nullable Long before,
		Pageable pageable
	);

	Optional<ChatChannelMessage> findByIdAndChatChannel(Long beforeChatMessageId, ChatChannel chatChannel);
}
