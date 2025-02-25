package one.colla.infra.mail;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import one.colla.infra.mail.events.InviteCodeSendMailEvent;
import one.colla.infra.mail.events.VerifyCodeSendMailEvent;
import one.colla.infra.mail.provider.InviteCodeContentProvider;
import one.colla.infra.mail.provider.VerifyCodeContentProvider;

@Component
@RequiredArgsConstructor
public class EmailEventListener {
	private final MailService mailService;

	@Async
	@TransactionalEventListener(classes = VerifyCodeSendMailEvent.class)
	public void onVerifyCodeSendMailEvent(VerifyCodeSendMailEvent event) {
		VerifyCodeContentProvider provider = VerifyCodeContentProvider.from(event.verifyCode());
		EmailEventHandler handler = new EmailEventHandler(mailService, provider);
		handler.handleSendMailEvent(event);
	}

	@Async
	@TransactionalEventListener(classes = InviteCodeSendMailEvent.class)
	public void onInviteCodeSendMailEvent(InviteCodeSendMailEvent event) {
		InviteCodeContentProvider provider = InviteCodeContentProvider.of(
			event.teamspaceName(),
			event.inviterName(),
			event.inviteCode(),
			event.teamspaceImageUrl(),
			event.numParticipants()
		);

		EmailEventHandler handler = new EmailEventHandler(mailService, provider);
		handler.handleSendMailEvent(event);
	}
}
