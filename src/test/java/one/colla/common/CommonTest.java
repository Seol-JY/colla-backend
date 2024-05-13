package one.colla.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import one.colla.common.builder.TestFixtureBuilder;

@ActiveProfiles("test")
@SpringBootTest
public abstract class CommonTest {
	@Autowired
	protected TestFixtureBuilder testFixtureBuilder;
}
