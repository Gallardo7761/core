package net.miarma.api.microservices.huertos.mail;

import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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
	
	public static void storeInSentFolder(Mail mail, String username, String password) {
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", ConfigManager.getInstance().getStringProperty("imap.server"));
            props.put("mail.imaps.port", ConfigManager.getInstance().getIntProperty("imap.port"));

            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(username, password);

            Folder sentFolder = store.getFolder("Sent");
            if (!sentFolder.exists()) {
                sentFolder.create(Folder.HOLDS_MESSAGES);
            }
            sentFolder.open(Folder.READ_WRITE);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail.getFrom()));
            for (String to : mail.getTo()) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            message.setSubject(mail.getSubject());
            message.setText(mail.getContent());

            sentFolder.appendMessages(new Message[]{message});
            sentFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }	
	
	public void sendEmail(Mail mail, String username, String password, Handler<AsyncResult<MailResult>> resultHandler) {		
		MailMessage message = new MailMessage()
			.setFrom(mail.getFrom())
			.setTo(mail.getTo())
			.setSubject(mail.getSubject())
			.setText(mail.getContent());
		getClientForUser(username, password).sendMail(message, ar -> {
			resultHandler.handle(ar);
			if(ar.succeeded()) {
				storeInSentFolder(mail, username, password);
			}
		});
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
		
		getClientForUser(username, password).sendMail(message, ar -> {
			resultHandler.handle(ar);
			if(ar.succeeded()) {
				storeInSentFolder(mail, username, password);
			}
		});
    }
}
