package one.colla.global.config.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

class ConditionalStringStripDeserializer extends JsonDeserializer<String> {
	private final JsonDeserializer<String> defaultDeserializer = new StringStripJsonDeserializer();
	private final JsonDeserializer<String> originalDeserializer = new JsonDeserializer<String>() {
		@Override
		public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
			return parser.getValueAsString();
		}
	};

	@Override
	public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		if (shouldSkipStrip(parser)) {
			return originalDeserializer.deserialize(parser, context);
		}
		return defaultDeserializer.deserialize(parser, context);
	}

	private boolean shouldSkipStrip(JsonParser parser) {
		return parser.getCurrentValue() != null
			&& parser.getCurrentValue().getClass().isAnnotationPresent(NoStrip.class);
	}
}
