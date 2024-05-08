package one.colla.common.util;

import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;
import one.colla.user.domain.OauthProvider;

@Slf4j
public class StringToProviderConverter implements Converter<String, OauthProvider> {
	@Override
	public OauthProvider convert(final String provider) {

		return OauthProvider.valueOf(provider.toUpperCase());
	}

}
