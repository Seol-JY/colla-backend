package one.colla.feed.vote.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

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
import one.colla.common.domain.CompositeKeyBase;
import one.colla.user.domain.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "vote_feed_selections")
public class VoteFeedSelection {

	@EmbeddedId
	private VoteFeedSelectionId voteFeedSelectionId;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@MapsId("voteFeedOptionId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vote_feed_option_id", nullable = false, updatable = false)
	private VoteFeedOption voteFeedOption;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public static class VoteFeedSelectionId extends CompositeKeyBase {
		@Column(name = "vote_feed_option_id")
		private Long voteFeedOptionId;

		@Column(name = "user_id")
		private Long userId;
	}
}
