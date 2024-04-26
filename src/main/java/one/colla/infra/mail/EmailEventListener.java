package one.colla.infra.mail;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import one.colla.infra.mail.events.VerifyCodeSendMailEvent;
import one.colla.infra.mail.provider.VerifyCodeContentProvider;

@RequiredArgsConstructor
public class EmailEventListener {
	private final MailService mailService;

	@Async
	@TransactionalEventListener(classes = VerifyCodeSendMailEvent.class)
	public void onVerifyCodeSendMailEvent(VerifyCodeSendMailEvent event) {
		VerifyCodeContentProvider provider = new VerifyCodeContentProvider(event.verifyCode());
		EmailEventHandler handler = new EmailEventHandler(mailService, provider);
		handler.handleSendMailEvent(event);
	}
}
