package one.colla.feed.scheduling.application.dto.request;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import one.colla.common.CommonTest;

class CreateSchedulingFeedDetailsValidationTest extends CommonTest {
	@Autowired
	private LocalValidatorFactoryBean validator;

	CreateSchedulingFeedDetails request;
	Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations;

	@Test
	@DisplayName("content가 null이어도 검증에 성공한다.")
	void testContentCanBeNull() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)1, (byte)10,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("dueAt가 현재 시간 이후라면 검증에 성공한다.")
	void testDueAtFuture() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)1, (byte)10,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("dueAt가 현재 시간 이전이라면 검증에 실패한다.")
	void testDueAtPast() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().minusDays(1), (byte)1, (byte)10,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);
			ConstraintViolation<CreateSchedulingFeedDetails> violation = violations.iterator().next();
			softly.assertThat(violation.getMessage()).isEqualTo("마감 시간은 현재시간 이후여야 합니다.");
		});
	}

	@Test
	@DisplayName("minTimeSegment가 null이면 검증에 실패한다.")
	void testMinTimeSegmentNotNull() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), null, (byte)10,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);
			ConstraintViolation<CreateSchedulingFeedDetails> violation = violations.iterator().next();
			softly.assertThat(violation.getMessage()).isEqualTo("최소 시간이 포함되어야 합니다.");
		});
	}

	@Test
	@DisplayName("minTimeSegment가 0 미만이면 검증에 실패한다.")
	void testMinTimeSegmentTooLow() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)-1, (byte)10,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);
			ConstraintViolation<CreateSchedulingFeedDetails> violation = violations.iterator().next();
			softly.assertThat(violation.getMessage()).isEqualTo("최소 시간은 0 이상이어야 합니다.");
		});
	}

	@Test
	@DisplayName("minTimeSegment가 47 초과이면 검증에 실패한다.")
	void testMinTimeSegmentTooHigh() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)48, (byte)10,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(2);
			softly.assertThat(violations.stream().map(ConstraintViolation::getMessage))
				.contains("최소 시간은 47 이하이어야 합니다.");
		});
	}

	@Test
	@DisplayName("maxTimeSegment가 null이면 검증에 실패한다.")
	void testMaxTimeSegmentNotNull() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)1, null,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);
			ConstraintViolation<CreateSchedulingFeedDetails> violation = violations.iterator().next();
			softly.assertThat(violation.getMessage()).isEqualTo("최대 시간이 포함되어야 합니다.");
		});
	}

	@Test
	@DisplayName("maxTimeSegment가 0 미만이면 검증에 실패한다.")
	void testMaxTimeSegmentTooLow() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)1, (byte)-1,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(2);
			softly.assertThat(violations.stream().map(ConstraintViolation::getMessage))
				.contains("최대 시간은 0 이상이어야 합니다.");
		});
	}

	@Test
	@DisplayName("maxTimeSegment가 47 초과이면 검증에 실패한다.")
	void testMaxTimeSegmentTooHigh() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)1, (byte)48,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);

			softly.assertThat(violations.stream().map(ConstraintViolation::getMessage))
				.contains("최대 시간은 47 이하이어야 합니다.");
		});
	}

	@Test
	@DisplayName("targetDates가 null이 아니며 각 날짜가 현재 시간 이후라면 검증에 성공한다.")
	void testTargetDatesFuture() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)1, (byte)10,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("targetDates의 일부 날짜가 현재 시간 이전이라면 검증에 실패한다.")
	void testTargetDatesPast() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)1, (byte)10,
			List.of(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);
			ConstraintViolation<CreateSchedulingFeedDetails> violation = violations.iterator().next();
			softly.assertThat(violation.getMessage()).isEqualTo("일자는 과거일 수 없습니다.");
		});
	}

	@Test
	@DisplayName("minTimeSegment가 maxTimeSegment보다 크면 검증에 실패한다.")
	void testMinTimeSegmentLessThanOrEqualToMaxTimeSegment() {
		// given
		request = new CreateSchedulingFeedDetails(LocalDateTime.now().plusDays(1), (byte)10, (byte)5,
			List.of(LocalDate.now().plusDays(1)));

		// when
		Set<ConstraintViolation<CreateSchedulingFeedDetails>> violations = validator.validate(request);

		// then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(violations).hasSize(1);
			ConstraintViolation<CreateSchedulingFeedDetails> violation = violations.iterator().next();
			softly.assertThat(violation.getMessage()).isEqualTo("minTimeSegment는 maxTimeSegment보다 작거나 같아야 합니다.");
		});
	}
}
