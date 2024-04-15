package one.colla.feed.normal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import one.colla.feed.common.domain.Feed;

@Entity
@DiscriminatorValue("NORMAL")
@Table(name = "normal_feeds")
public class NormalFeed extends Feed {

	@Column(name = "content")
	private String content;

}
