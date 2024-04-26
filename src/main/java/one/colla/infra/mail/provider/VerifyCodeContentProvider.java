package one.colla.infra.mail.provider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerifyCodeContentProvider implements MailContentProvider {
	final String verifyCode;

	public static VerifyCodeContentProvider from(String verifyCode) {
		return new VerifyCodeContentProvider(verifyCode);
	}

	@Override
	public String getSubject() {
		return "[Colla] 회원가입 인증 코드 입니다.";
	}

	@Override
	public String getContent() {
		return "인증코드는 " + verifyCode + " 입니다.";
	}
}
