package one.colla.common.util;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomCodeGenerator {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private final Random random = new Random();

	public String generateRandomString(final int length) {
		final StringBuilder randomString = new StringBuilder();

		for (int i = 0; i < length; i++) {
			final int randomIndex = random.nextInt(CHARACTERS.length());
			final char randomChar = CHARACTERS.charAt(randomIndex);
			randomString.append(randomChar);
		}

		return randomString.toString();
	}
}
