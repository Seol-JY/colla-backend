package one.colla.teamspace.domain.vo;

import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import one.colla.common.domain.vo.Url;

@EqualsAndHashCode(callSuper = false)
@Getter
public class TeamspaceProfileImageUrl extends Url {

	@Column(name = "profile_image_url")
	private final String value;

	public TeamspaceProfileImageUrl() {
		this.value = null;
	}

	public TeamspaceProfileImageUrl(final String value) {
		validate(value);
		this.value = value;
	}

	public TeamspaceProfileImageUrl change(final String newUrl) {
		return new TeamspaceProfileImageUrl(newUrl);
	}

}
