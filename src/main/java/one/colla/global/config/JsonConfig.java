package one.colla.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class JsonConfig {
	@Bean
	public ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder
			.json()
			.modules(customJsonDeserializeModule())
			.build();
	}

	private SimpleModule customJsonDeserializeModule() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(String.class, new StringStripJsonDeserializer());

		return module;
	}
}

