package one.colla.global.config.json;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
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
		module.setDeserializerModifier(new BeanDeserializerModifier() {
			@Override
			public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
				BeanDescription beanDesc,
				JsonDeserializer<?> deserializer) {
				if (beanDesc.getBeanClass() == String.class) {
					return new ConditionalStringStripDeserializer();
				}
				return deserializer;
			}
		});
		return module;
	}
}
