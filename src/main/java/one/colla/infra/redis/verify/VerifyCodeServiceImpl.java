package one.colla.infra.redis.verify;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl implements VerifyCodeService {
	private final VerifyCodeRepository verifyCodeRepository;

	@Override
	public void save(VerifyCode verifyCode) {
		verifyCodeRepository.save(verifyCode);
	}

	@Override
	public Optional<VerifyCode> findByEmail(String email) {
		return verifyCodeRepository.findById(email);
	}
}
