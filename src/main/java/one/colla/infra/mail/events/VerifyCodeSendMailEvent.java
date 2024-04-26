package one.colla.infra.mail.events;

public record VerifyCodeSendMailEvent(
	String email,
	String verifyCode
) implements SendMailEvent {
	@Override
	public String getEmail() {
		return email;
	}
}
