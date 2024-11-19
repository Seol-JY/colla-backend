package one.colla.global.config.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public class StringStripJsonDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {
	private boolean skipStrip = false;

	public StringStripJsonDeserializer() {
		this(false);
	}

	private StringStripJsonDeserializer(boolean skipStrip) {
		this.skipStrip = skipStrip;
	}

	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		String value = jp.getValueAsString();

		if (value == null) {
			return null;
		}

		if (skipStrip) {
			return value;
		}

		String valueStripped = value.strip();
		return !valueStripped.isEmpty() ? valueStripped : null;
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
		if (property != null) {
			NoStrip annotation = property.getAnnotation(NoStrip.class);
			if (annotation != null) {
				return new StringStripJsonDeserializer(true);
			}
		}

		return new StringStripJsonDeserializer(false);
	}
}

