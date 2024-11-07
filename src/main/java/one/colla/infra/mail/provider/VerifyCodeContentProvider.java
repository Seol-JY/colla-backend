package one.colla.infra.mail.provider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerifyCodeContentProvider implements MailContentProvider {
	private static final String TEMPLATE_PATH = "templates/verify-email-template.html";

	private static final String EMAIL_TEMPLATE = loadTemplate();

	private final String verifyCode;

	private static String loadTemplate() {
		try {
			var classLoader = VerifyCodeContentProvider.class.getClassLoader();
			try (var inputStream = classLoader.getResourceAsStream(TEMPLATE_PATH)) {
				if (inputStream == null) {
					throw new IllegalStateException("이메일 템플릿 파일을 찾을 수 없습니다: " + TEMPLATE_PATH);
				}
				return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			throw new IllegalStateException("이메일 템플릿을 로드하는데 실패했습니다", e);
		}
	}

	public static VerifyCodeContentProvider from(String verifyCode) {
		return new VerifyCodeContentProvider(verifyCode);
	}

	@Override
	public String getSubject() {
		return "[Colla] 회원가입 인증 코드 입니다.";
	}

	@Override
	public String getContent() {
		return generateEmailContent();
	}

	private String generateEmailContent() {
		return String.format(EMAIL_TEMPLATE, verifyCode);
	}
}
