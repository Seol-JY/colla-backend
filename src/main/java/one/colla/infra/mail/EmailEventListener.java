package one.colla.infra.mail;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import one.colla.infra.mail.events.InviteCodeSendMailEvent;
import one.colla.infra.mail.events.VerifyCodeSendMailEvent;
import one.colla.infra.mail.provider.InviteCodeContentProvider;
import one.colla.infra.mail.provider.VerifyCodeContentProvider;

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
		InviteCodeContentProvider provider = InviteCodeContentProvider.of(event.teamspaceName(), event.inviterName(),
			event.inviteCode());
		EmailEventHandler handler = new EmailEventHandler(mailService, provider);
		handler.handleSendMailEvent(event);
	}
}
