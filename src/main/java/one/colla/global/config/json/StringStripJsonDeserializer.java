package one.colla.global.config.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class StringStripJsonDeserializer extends JsonDeserializer<String> {
	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		String value = jp.getValueAsString();

		if (value == null) {
			return null;
		}

		String valueStripped = value.strip();

		return !valueStripped.isEmpty() ? valueStripped : null;
	}
}
