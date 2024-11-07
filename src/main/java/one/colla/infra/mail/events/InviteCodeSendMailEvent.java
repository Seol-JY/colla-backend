package one.colla.infra.mail.events;

import javax.annotation.Nullable;

public record InviteCodeSendMailEvent(
	String email,
	String teamspaceName,
	String inviterName,
	String inviteCode,
	@Nullable
	String teamspaceImageUrl,
	Integer numParticipants
) implements SendMailEvent {
	@Override
	public String getEmail() {
		return email;
	}
}
