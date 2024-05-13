package one.colla.common.application.dto.request;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import one.colla.common.CommonTest;

class PreSignedUploadDtoTest extends CommonTest {

	final String VALID_ATTACHMENT_NAME = "valid_attachment.jpg";

	@Autowired
	private LocalValidatorFactoryBean validator;

	PreSignedUploadDto request;

	@Test
	@DisplayName("도메인 타입이 null일 경우 validation에 실패한다.")
	void testValidDomainTypeIsNull() {
		// given
		request = new PreSignedUploadDto(null, 1L, VALID_ATTACHMENT_NAME);

		// when
		Set<ConstraintViolation<PreSignedUploadDto>> violations = validator.validate(request);

		// then
		assertThat(violations).extracting("message").contains("도메인 타입을 입력해주세요.");
	}

	@Test
	@DisplayName("도메인 타입이 팀스페이스일 경우 teamspaceId가 있다면 Validation에 성공한다.")
	void testValidTeamspaceId() {
		// given
		request = new PreSignedUploadDto(DomainType.TEAMSPACE, 1L, VALID_ATTACHMENT_NAME);

		// when
		Set<ConstraintViolation<PreSignedUploadDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
		assertThat(request.isTeamspaceIdValid()).isTrue();
	}

	@Test
	@DisplayName("도메인 타입이 팀스페이스일 경우 teamspaceId가 없다면 Validation에 실패한다.")
	void testInvalidTeamspaceId() {
		// given
		request = new PreSignedUploadDto(DomainType.TEAMSPACE, null, VALID_ATTACHMENT_NAME);

		// when
		Set<ConstraintViolation<PreSignedUploadDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isNotEmpty();
		assertThat(violations).extracting("message").contains("도메인 타입이 팀스페이스일 경우 팀스페이스 id를 입력하셔야 합니다.");
		assertThat(request.isTeamspaceIdValid()).isFalse();
	}

	@Test
	@DisplayName("도메인 타입이 유저일 경우 teamspaceId가 있다면 Validation에 실패한다.")
	void testInvalidDomainTypeUserWithTeamspaceId() {
		// given
		request = new PreSignedUploadDto(DomainType.USER, 100L, VALID_ATTACHMENT_NAME);

		// when
		Set<ConstraintViolation<PreSignedUploadDto>> violations = validator.validate(request);

		// then
		assertThat(violations).extracting("message").contains("도메인 타입이 유저일 경우 팀스페이스 id를 입력하시면 안됩니다.");
	}

	@Test
	@DisplayName("도메인 타입이 유저일 경우 teamspaceId가 없다면 Validation에 성공한다.")
	void testValidDomainTypeUserNoTeamspaceId() {
		// given
		request = new PreSignedUploadDto(DomainType.USER, null, VALID_ATTACHMENT_NAME);

		// when
		Set<ConstraintViolation<PreSignedUploadDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
		assertThat(request.isTeamspaceIdNullValid()).isTrue();
	}

	@Test
	@DisplayName("파일 이름이 null인 경우 Validation에 실패한다.")
	void testFilenameEmpty() {
		// given
		request = new PreSignedUploadDto(DomainType.USER, null, null);

		// when
		Set<ConstraintViolation<PreSignedUploadDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isNotEmpty();
		assertThat(violations).extracting("message").contains("파일 이름을 입력해주세요.");
	}

	@Test
	@DisplayName("확장자 없는 파일 이름일 경우 Validation에 실패한다.")
	void testFilenameWithoutExtension() {
		// given
		request = new PreSignedUploadDto(DomainType.USER, null, "userProfile");

		// when
		Set<ConstraintViolation<PreSignedUploadDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isNotEmpty();
		assertThat(violations).extracting("message").contains("파일 이름에는 확장자가 포함되어야 합니다.");
	}

	@Test
	@DisplayName("유효한 파일 이름일 경우 Validation에 성공한다.")
	void testFilenameWithExtension() {
		// given
		request = new PreSignedUploadDto(DomainType.USER, null, VALID_ATTACHMENT_NAME);

		// when
		Set<ConstraintViolation<PreSignedUploadDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

}
