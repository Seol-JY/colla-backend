package one.colla.feed.normal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.feed.common.application.dto.request.CommonCreateFeedRequest;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.normal.application.dto.request.CreateNormalFeedDetails;
import one.colla.teamspace.domain.Teamspace;
import one.colla.user.domain.User;

@Getter
@Entity
@DiscriminatorValue("NORMAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "normal_feeds")
public class NormalFeed extends Feed {

	@Column(name = "content")
	private String content;

	public static NormalFeed of(
		final User user,
		final Teamspace teamspace,
		final CommonCreateFeedRequest<CreateNormalFeedDetails> createNormalFeedRequest
	) {
		CreateNormalFeedDetails createNormalFeedDetails = createNormalFeedRequest.details();

		return new NormalFeed(
			user,
			teamspace,
			createNormalFeedRequest,
			createNormalFeedDetails.content()
		);
	}

	private NormalFeed(
		final User user,
		final Teamspace teamspace,
		final CommonCreateFeedRequest<? extends CreateFeedDetails> commonCreateFeedRequest,
		final String content
	) {
		super(user, teamspace, commonCreateFeedRequest);
		this.content = content;
	}
}
