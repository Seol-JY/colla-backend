package one.colla.infra.mail.events;

public record InviteCodeSendMailEvent(
	String email,
	String teamspaceName,
	String inviterName,
	String inviteCode

) implements SendMailEvent {
	@Override
	public String getEmail() {
		return email;
	}
}
