package one.colla.infra.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
	private final String host;
	private final int port;
	private final String username;
	private final String password;

	public MailConfig(
		@Value("${spring.mail.host}") String host,
		@Value("${spring.mail.port}") int port,
		@Value("${spring.mail.username}") String username,
		@Value("${spring.mail.password}") String password
	) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	@Bean
	public JavaMailSender javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		javaMailSender.setHost(host);
		javaMailSender.setPort(port);
		javaMailSender.setUsername(username);
		javaMailSender.setPassword(password);
		javaMailSender.setJavaMailProperties(getMailProperties());

		return javaMailSender;
	}

	private Properties getMailProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.debug", "false");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.connectiontimeout", "5000");
		properties.setProperty("mail.smtp.timeout", "3000");
		properties.setProperty("mail.smtp.writetimeout", "5000");

		return properties;
	}
}
