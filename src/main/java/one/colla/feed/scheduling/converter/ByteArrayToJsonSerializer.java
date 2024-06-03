package one.colla.feed.scheduling.converter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ByteArrayToJsonSerializer extends JsonSerializer<byte[]> {
	@Override
	public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartArray();
		for (byte b : value) {
			gen.writeNumber(b);
		}
		gen.writeEndArray();
	}
}
