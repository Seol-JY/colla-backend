package one.colla.teamspace.application.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import one.colla.common.CommonTest;
import one.colla.teamspace.application.dto.request.UpdateTeamspaceSettingsRequest;

class UpdateTeamspaceSettingsRequestValidationTest extends CommonTest {
	@Autowired
	private LocalValidatorFactoryBean validator;

	UpdateTeamspaceSettingsRequest request;
	Set<ConstraintViolation<UpdateTeamspaceSettingsRequest>> violations;

	@Test
	@DisplayName("모든 값은 Null 이 될 수 있다.")
	void testCanNull() {
		// given
		request = new UpdateTeamspaceSettingsRequest(null, null, null);

		// when
		Set<ConstraintViolation<UpdateTeamspaceSettingsRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("ProfileImageUrl 이 Null 이 아니라면 Validation 에 성공한다.")
	void testProfileImageUrlSuccessful() {
		// given
		request = new UpdateTeamspaceSettingsRequest("https://wwww.example.com", null, null);

		// when
		Set<ConstraintViolation<UpdateTeamspaceSettingsRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("ProfileImageUrl 이 Null 이 아니라면 URL 형식이 아닐때 예외가 발생한다.")
	void testProfileImageUrlFailure() {
		// given
		request = new UpdateTeamspaceSettingsRequest("htt://wwww.example.com", null, null);

		// when
		Set<ConstraintViolation<UpdateTeamspaceSettingsRequest>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);
			ConstraintViolation<UpdateTeamspaceSettingsRequest> violation = violations.iterator().next();
			softly.assertThat(violation.getMessage()).isEqualTo("프로필 이미지 URL 형식이 올바르지 않습니다.");
		});
	}

	@Test
	@DisplayName("Name 이 Null 이 아니라면 Validation 에 성공한다.")
	void testNameSuccessful() {
		// given
		request = new UpdateTeamspaceSettingsRequest(null, "팀스페이스이름", null);

		// when
		Set<ConstraintViolation<UpdateTeamspaceSettingsRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("Name 이 Null 이 아니라면 팀스페이스 이름 길이가 맞지 않는다면 예외가 발생한다.")
	void testNameFailure() {
		// given
		request = new UpdateTeamspaceSettingsRequest(null, "팀", null);

		// when
		Set<ConstraintViolation<UpdateTeamspaceSettingsRequest>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);
			ConstraintViolation<UpdateTeamspaceSettingsRequest> violation = violations.iterator().next();
			softly.assertThat(violation.getMessage()).isEqualTo("팀스페이스 길이는 2자 이상 20자 이하여야 합니다.");
		});
	}

	@Test
	@DisplayName("Users 이 빈 배열이라도 Validation 에 성공한다.")
	void testUsersSuccessful() {
		// given
		request = new UpdateTeamspaceSettingsRequest("https://example.com", "팀스페이스 이름", null);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("Users 이 빈 배열이거나 null 이 아니라면, UserUpdateInfo 의 Validation 을 진행하며, 실패시 예외가 발생한다..")
	void testUsersFailure() {
		// given
		request = new UpdateTeamspaceSettingsRequest("https://example.com", "팀스페이스 이름",
			List.of(new UpdateTeamspaceSettingsRequest.UserUpdateInfo(null, null)));

		// when
		violations = validator.validate(request);

		// then
		Set<String> messages = violations.stream()
			.map(ConstraintViolation::getMessage)
			.collect(Collectors.toSet());

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(2);
			softly.assertThat(messages).contains("tagId는 null 일 수 없습니다.");
			softly.assertThat(messages).contains("userId는 null 일 수 없습니다.");
		});
	}
}

