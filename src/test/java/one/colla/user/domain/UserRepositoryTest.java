package one.colla.user.domain;

import static one.colla.common.fixtures.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import one.colla.common.RepositoryTest;
import one.colla.user.domain.vo.Email;

public class UserRepositoryTest extends RepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@DisplayName("이메일로 사용자를 찾을 수 있다.")
	@Test
	public void testFindByEmail() {
		// Given
		final User USER1 = testFixtureBuilder.buildUser(USER1());

		// When
		Email emailVo = new Email(USER1.getEmailValue());
		Optional<User> foundUser = userRepository.findByEmail(emailVo);

		// Then
		assertThat(foundUser.isPresent()).isTrue();
		assertThat(foundUser.get().getEmail()).isEqualTo(emailVo);
	}

	@DisplayName("저장되지 않은 이메일로 사용자를 찾을 수 없다.")
	@Test
	public void testFindByEmailNotFound() {
		// Given
		Email email = new Email("notfound@example.com");

		// When
		Optional<User> foundUser = userRepository.findByEmail(email);

		// Then
		assertThat(foundUser.isPresent()).isFalse();
	}
}
