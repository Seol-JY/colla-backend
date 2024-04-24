package one.colla.infra.redis.invite;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface InviteCodeRepository extends CrudRepository<InviteCode, Long> {
	boolean existsByCode(String generatedCode);

	Optional<InviteCode> findByCode(String code);
}
