package one.colla.feed.common.util;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtil {
	public static final LocalDateTime INFINITY = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

	/**
	 * 마감 일시가 현재 시간 기준으로 지났는지 판단합니다.
	 *
	 * @param deadline 마감 일시
	 * @return 마감 시간이 지났으면 true, 그렇지 않으면 false
	 */
	public static boolean isDeadlinePassed(LocalDateTime deadline) {
		LocalDateTime now = LocalDateTime.now();
		return deadline.isBefore(now);
	}
}
