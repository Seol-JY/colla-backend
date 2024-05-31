package one.colla.feed.common.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import one.colla.user.domain.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Optional<Comment> findByIdAndUserAndFeed(Long commentId, User user, Feed feed);

	Optional<Comment> findByIdAndFeed(Long commentId, Feed feed);
}
