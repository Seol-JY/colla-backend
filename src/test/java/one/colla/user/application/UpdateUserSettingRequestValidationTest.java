package one.colla.user.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import one.colla.common.CommonTest;
import one.colla.user.application.dto.request.UpdateUserSettingRequest;
import one.colla.user.domain.CommentNotification;

class UpdateUserSettingRequestValidationTest extends CommonTest {
	@Autowired
	private LocalValidatorFactoryBean validator;

	UpdateUserSettingRequest request;
	Set<ConstraintViolation<UpdateUserSettingRequest>> violations;

	@Test
	@DisplayName("모든 값이 Null일 수 있다")
	void testAllValuesCanBeNull() {
		// given
		request = new UpdateUserSettingRequest(null, null, null, null);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("ProfileImageUrl 이 유효한 URL 형식이면 Validation 에 성공한다")
	void testProfileImageUrlValid() {
		// given
		request = new UpdateUserSettingRequest("https://www.example.com", null, null, null);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("ProfileImageUrl 이 유효하지 않은 URL 형식일 때 예외가 발생한다")
	void testProfileImageUrlInvalid() {
		// given
		request = new UpdateUserSettingRequest("htp://example", null, null, null);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isNotEmpty();
		assertThat(violations.iterator().next().getMessage()).isEqualTo("프로필 이미지 URL 형식이 올바르지 않습니다.");
	}

	@Test
	@DisplayName("Username 이 유효한 길이 범위 내에 있으면 Validation에 성공한다")
	void testUsernameLengthValid() {
		// given
		request = new UpdateUserSettingRequest(null, "유효한닉네임", null, null);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("Username 길이가 유효 범위를 벗어나면 예외가 발생한다")
	void testUsernameLengthInvalid() {
		// given
		request = new UpdateUserSettingRequest(null, "닉", null, null);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isNotEmpty();
		assertThat(violations.iterator().next().getMessage()).isEqualTo("닉네임은 2자 이상 50자 이하이어야 합니다.");
	}

	@Test
	@DisplayName("이메일 구독 여부가 설정되어 있으면 Validation에 성공한다")
	void testEmailSubscriptionSet() {
		// given
		request = new UpdateUserSettingRequest(null, null, true, null);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("CommentNotification이 설정되어 있으면 Validation에 성공한다")
	void testCommentNotificationSet() {
		request = new UpdateUserSettingRequest(null, null, null, CommentNotification.ALL);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("모든 필드가 유효할 때 Validation에 성공한다")
	void testAllFieldsValid() {
		// given
		request = new UpdateUserSettingRequest("https://www.example.com", "유효한닉네임", true, CommentNotification.ALL);

		// when
		violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("여러 필드가 동시에 유효하지 않을 때 여러 예외가 발생한다")
	void testMultipleFieldsInvalid() {
		// given
		request = new UpdateUserSettingRequest("htttp://bad-url", "x", null, null);

		// when
		violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(2);
			Set<String> messages = violations.stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.toSet());
			softly.assertThat(messages).contains("프로필 이미지 URL 형식이 올바르지 않습니다.");
			softly.assertThat(messages).contains("닉네임은 2자 이상 50자 이하이어야 합니다.");
		});
	}
}
