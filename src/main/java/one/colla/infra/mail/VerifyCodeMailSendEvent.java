package one.colla.infra.mail;

public record VerifyCodeMailSendEvent(
	String email,
	String verifyCode
) {
}
