package one.colla.feed.common.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class LocalDateListDeserializer extends JsonDeserializer<List<LocalDate>> {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public List<LocalDate> deserialize(JsonParser p, DeserializationContext ctxt) throws
		IOException {
		List<LocalDate> localDates = new ArrayList<>();
		JsonNode node = p.getCodec().readTree(p);
		if (node.isArray()) {
			for (JsonNode dateNode : node) {
				localDates.add(LocalDate.parse(dateNode.asText(), formatter));
			}
		}
		return localDates;
	}
}
