package one.colla.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import one.colla.common.builder.BuilderSupporter;
import one.colla.common.builder.TestFixtureBuilder;
import one.colla.global.config.JpaConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import(value = {TestFixtureBuilder.class, BuilderSupporter.class, JpaConfig.class})
public abstract class RepositoryTest {

	@Autowired
	protected TestFixtureBuilder testFixtureBuilder;
}
