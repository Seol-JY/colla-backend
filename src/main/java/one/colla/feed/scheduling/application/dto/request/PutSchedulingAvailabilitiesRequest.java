package one.colla.feed.scheduling.application.dto.request;

import java.time.LocalDate;
import java.util.Map;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import one.colla.feed.common.application.dto.request.CreateFeedDetails;

public record PutSchedulingAvailabilitiesRequest(

	@NotNull(message = "Availabilities 목록이 포함되어야 합니다.")
	@Size(min = 1, message = "Availabilities 목록은 최소 1개 이상의 날짜를 포함해야 합니다.")
	Map<@FutureOrPresent(message = "일자는 과거일 수 없습니다.") LocalDate, byte[]> availabilities

) implements CreateFeedDetails {

	@AssertTrue(message = "Availabilities의 배열 크기는 정확히 48이어야 합니다.")
	boolean isByteArraySizeValid() {
		if (availabilities == null) {
			return true;
		}
		for (byte[] array : availabilities.values()) {
			if (array.length != 48) {
				return false;
			}
		}
		return true;
	}

	@AssertTrue(message = "Availabilities의 배열 내부에는 숫자 0과 1만 존재해야 합니다.")
	boolean isByteArrayContentValid() {
		if (availabilities == null) {
			return true;
		}
		for (byte[] array : availabilities.values()) {
			for (byte b : array) {
				if (b != 0 && b != 1) {
					return false;
				}
			}
		}
		return true;
	}
}
