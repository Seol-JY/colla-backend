package one.colla.file.domain.vo;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.colla.global.exception.VoException;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class AttachmentName {
	private static final int MAX_LENGTH = 255;

	@Column(name = "name", nullable = false, length = 255)
	private String value;

	private AttachmentName(final String value) {
		validate(value);
		this.value = value;
	}

	public static AttachmentName from(String attachmentName) {
		return new AttachmentName(attachmentName);
	}

	private void validate(final String value) {
		if (Objects.isNull(value)) {
			throw new VoException("Attachment 이름은 null 일 수 없습니다.");
		}
		if (value.isBlank()) {
			throw new VoException("Attachment 이름은 공백일 수 없습니다.");
		}
		if (value.length() > MAX_LENGTH) {
			throw new VoException("Attachment 이름은 " + MAX_LENGTH + "자 이하여야 합니다.");
		}
	}
}
