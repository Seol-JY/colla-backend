package one.colla.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthApprovalRepository extends JpaRepository<OauthApproval, Long> {
}
