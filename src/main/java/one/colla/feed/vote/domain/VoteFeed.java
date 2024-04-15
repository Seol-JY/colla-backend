package one.colla.feed.vote.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import one.colla.feed.common.domain.Feed;

@Entity
@DiscriminatorValue("VOTE")
@Table(name = "vote_feeds")
public class VoteFeed extends Feed {

	@Column(name = "plural", nullable = false)
	private boolean plural;

	@Column(name = "anonymous", nullable = false)
	private boolean anonymous;

	@Column(name = "num_of_participants", nullable = false)
	private Byte numOfParticipants;

	@Column(name = "due_at", nullable = false)
	private LocalDateTime dueAt;

	@OneToMany(mappedBy = "voteFeed", fetch = FetchType.LAZY)
	private final List<VoteFeedOption> voteFeedOptions = new ArrayList<>();

}
