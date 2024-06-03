package one.colla.feed.scheduling.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ByteArrayConverter implements AttributeConverter<byte[], String> {

	@Override
	public String convertToDatabaseColumn(byte[] attribute) {
		if (attribute == null || attribute.length == 0) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < attribute.length; i++) {
			sb.append(attribute[i]);
			if (i < attribute.length - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public byte[] convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.equals("[]") || dbData.isEmpty()) {
			return new byte[0];
		}
		String[] stringBytes = dbData.substring(1, dbData.length() - 1).split(",");
		byte[] bytes = new byte[stringBytes.length];
		for (int i = 0; i < stringBytes.length; i++) {
			bytes[i] = Byte.parseByte(stringBytes[i].trim());
		}
		return bytes;
	}
}
