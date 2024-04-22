package one.colla.infra.redis.forbidden;

import org.springframework.data.repository.CrudRepository;

public interface ForbiddenTokenRepository extends CrudRepository<ForbiddenToken, String> {
}
