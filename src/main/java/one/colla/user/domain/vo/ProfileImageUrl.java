package one.colla.user.domain.vo;

import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import one.colla.common.domain.vo.Url;

@EqualsAndHashCode(callSuper = false)
@Getter
public class ProfileImageUrl extends Url {

	@Column(name = "profile_image_url")
	private String value;

	public ProfileImageUrl() {
		this.value = null;
	}

	public ProfileImageUrl(final String value) {
		validate(value);
		this.value = value;
	}

	public ProfileImageUrl change(final String newUrl) {
		return new ProfileImageUrl(newUrl);
	}

}
