package one.colla.infra.redis.verify;

import java.util.Optional;

public interface VerifyCodeService {
	/**
	 * verify code를 redis에 저장한다.
	 *
	 * @param verifyCode : {@link VerifyCode}
	 */
	void save(VerifyCode verifyCode);

	/**
	 *
	 * @param email
	 * */
	Optional<VerifyCode> findByEmail(String email);  // 이메일로 검증 코드를 조회하는 메서드
}
