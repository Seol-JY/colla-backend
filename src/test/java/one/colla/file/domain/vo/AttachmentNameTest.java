package one.colla.file.domain.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import one.colla.global.exception.VoException;

class AttachmentNameTest {
	@Test
	@DisplayName("두 객체의 값이 같으면 같은 객체이다.")
	void testEqualsAndHashCode1() {
		// given
		String input = "attachmentName";

		// when
		AttachmentName attachmentName1 = AttachmentName.from(input);
		AttachmentName attachmentName2 = AttachmentName.from(input);

		// then
		assertThat(attachmentName1).isEqualTo(attachmentName2);
	}

	@Test
	@DisplayName("두 객체의 값이 다르면 다른 객체이다.")
	void testEqualsAndHashCode2() {
		// given
		String input1 = "attachmentName1";
		String input2 = "attachmentName2";

		// when
		AttachmentName attachmentName1 = AttachmentName.from(input1);
		AttachmentName attachmentName2 = AttachmentName.from(input2);

		// then
		assertThat(attachmentName1).isNotEqualTo(attachmentName2);
	}

	@Test
	@DisplayName("유효한 Attachment 이름을 생성할 수 있다.")
	void testValidAttachmentName() {
		// given
		String input = "ValidAttachmentName";

		// when
		AttachmentName attachmentName = AttachmentName.from(input);

		// then
		assertThat(attachmentName.getValue()).isEqualTo(input);
	}

	@Test
	@DisplayName("Attachment 이름은 255자 초과일 수 없다.")
	void testAttachmentNameTooLong() {
		// given
		String input = "L".repeat(255 + 1);

		// when & then
		assertThatThrownBy(() -> AttachmentName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("Attachment 이름은 255자 이하여야 합니다.");
	}

	@Test
	@DisplayName("Attachment 이름은 null이 될 수 없다.")
	void testAttachmentNameNull() {
		// given
		String input = null;

		// when & then
		assertThatThrownBy(() -> AttachmentName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("Attachment 이름은 null 일 수 없습니다.");
	}

	@Test
	@DisplayName("Attachment 이름은 공백일 수 없다.")
	void testAttachmentNameBlank() {
		// given
		String input = "   ";

		// when & then
		assertThatThrownBy(() -> AttachmentName.from(input))
			.isInstanceOf(VoException.class)
			.hasMessageContaining("Attachment 이름은 공백일 수 없습니다.");
	}
}
