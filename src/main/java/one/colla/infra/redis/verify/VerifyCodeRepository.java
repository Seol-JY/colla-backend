package one.colla.infra.redis.verify;

import org.springframework.data.repository.CrudRepository;

public interface VerifyCodeRepository extends CrudRepository<VerifyCode, String> {
}
