package one.colla.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import jakarta.transaction.Transactional;
import one.colla.common.builder.TestFixtureBuilder;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RecordApplicationEvents
public abstract class ServiceTest {

	@Autowired
	protected TestFixtureBuilder testFixtureBuilder;

	@Autowired
	protected ApplicationEvents applicationEvents;

}
