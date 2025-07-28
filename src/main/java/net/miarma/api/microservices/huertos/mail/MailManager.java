package net.miarma.api.microservices.huertos.mail;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.mail.LoginOption;
import io.vertx.ext.mail.MailAttachment;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;
import io.vertx.ext.mail.StartTLSOptions;
import net.miarma.api.common.ConfigManager;

public class MailManager {
	private final Vertx vertx;
	private final String smtpHost;
	private final int smtpPort;
	private final Map<String, MailClient> clientCache = new ConcurrentHashMap<>();

	public MailManager(Vertx vertx) {
		this.vertx = vertx;
		this.smtpHost = ConfigManager.getInstance().getStringProperty("smtp.server");
		this.smtpPort = ConfigManager.getInstance().getIntProperty("smtp.port");
	}
	
	private MailClient getClientForUser(String username, String password) {
		return clientCache.computeIfAbsent(username, _ -> {
			MailConfig config = new MailConfig()
					.setHostname(smtpHost)
					.setPort(smtpPort)
					.setStarttls(StartTLSOptions.REQUIRED)
					.setLogin(LoginOption.REQUIRED)
					.setUsername(username)
					.setPassword(password)
					.setKeepAlive(true);
			return MailClient.createShared(vertx, config, "mail-pool-" + username);
		});
	}
	
	public void sendEmail(Mail mail, String username, String password, Handler<AsyncResult<MailResult>> resultHandler) {
		MailMessage message = new MailMessage()
			.setFrom(mail.getFrom())
			.setTo(mail.getTo())
			.setSubject(mail.getSubject())
			.setText(mail.getContent());
		getClientForUser(username, password).sendMail(message, resultHandler);
    }
	
	public void sendEmailWithAttachment(Mail mail, List<Attachment> attachments, String username, String password,
			Handler<AsyncResult<MailResult>> resultHandler) {
		List<MailAttachment> mailAttachments = attachments.stream().map(a -> {
            return MailAttachment.create()
                .setData(Buffer.buffer(a.getData()))
                .setName(a.getFilename())
                .setContentType(a.getMimeType())
                .setDisposition("attachment");
        }).collect(Collectors.toList());
		
		MailMessage message = new MailMessage()
				.setFrom(mail.getFrom())
				.setTo(mail.getTo())
				.setSubject(mail.getSubject())
				.setText(mail.getContent())
				.setAttachment(mailAttachments);
		
		getClientForUser(username, password).sendMail(message, resultHandler);
    }
}
