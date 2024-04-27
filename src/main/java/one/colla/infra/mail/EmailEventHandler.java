package one.colla.infra.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import one.colla.infra.mail.events.SendMailEvent;
import one.colla.infra.mail.provider.MailContentProvider;

@Slf4j
public class EmailEventHandler {
	private final MailService mailService;
	private final MailContentProvider contentProvider;

	public EmailEventHandler(MailService mailService, MailContentProvider contentProvider) {
		this.mailService = mailService;
		this.contentProvider = contentProvider;
	}

	public void handleSendMailEvent(SendMailEvent event) {
		MimeMessage message = mailService.createMessage(
			event.getEmail(),
			contentProvider.getSubject(),
			contentProvider.getContent()
		);
		log.info("Send mail event {}", event);
		mailService.sendMail(message);
	}
}
