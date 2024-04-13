package one.colla.teamspace.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.common.domain.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "teamspaces")
public class Teamspace extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "teamspace", fetch = FetchType.LAZY)
	private final List<UserTeamspace> userTeamspaces = new ArrayList<>();

	@OneToMany(mappedBy = "teamspace", fetch = FetchType.LAZY)
	private final List<Tag> tags = new ArrayList<>();

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

}
