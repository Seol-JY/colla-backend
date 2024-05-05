package one.colla.common.util;

import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;
import one.colla.user.domain.Provider;

@Slf4j
public class StringToEnumConverter implements Converter<String, Provider> {
	@Override
	public Provider convert(final String provider) {

		return Provider.valueOf(provider.toUpperCase());
	}

}
