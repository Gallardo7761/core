package net.miarma.api.microservices.huertos.mail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import net.miarma.api.common.ConfigManager;

public class ImapReader {

	public static List<Mail> listInboxEmails(String folder, String username, String password) throws Exception {
	    Properties props = new Properties();
	    props.put("mail.store.protocol", "imaps");
	    System.setProperty("jakarta.mail.streamprovider", "jakarta.mail.util.DefaultStreamProvider");

	    Session session = Session.getInstance(props, null);
	    Store store = session.getStore();
	    store.connect(
	        ConfigManager.getInstance().getStringProperty("imap.server"),
	        ConfigManager.getInstance().getIntProperty("imap.port"),
	        username, password
	    );

	    Folder f = store.getFolder(folder);
	    f.open(Folder.READ_ONLY);

	    Message[] messages = f.getMessages();
	    List<Mail> emails = new java.util.ArrayList<>();

	    for (Message message : messages) {
	        String from = message.getFrom() != null ? message.getFrom()[0].toString() : "Desconocido";
	        from = MimeUtility.decodeText(from);
	        List<String> toList = new java.util.ArrayList<>();
	        if (message.getAllRecipients() != null) {
	            for (var address : message.getAllRecipients()) {
	                toList.add(address.toString());
	            }
	        }

	        String subject = message.getSubject();
	        String content = extractContent(message);
	        LocalDateTime date = message.getSentDate() != null
	                ? message.getSentDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
	                : LocalDateTime.now();

	        List<Attachment> attachments = extractAttachments(message);

	        emails.add(new Mail(from, toList, subject, content, date, attachments));
	    }

	    f.close(false);
	    store.close();

	    return emails;
	}
	
	public static Mail getEmailByIndex(String folder, String username, String password, int index) throws Exception {
	    Properties props = new Properties();
	    props.put("mail.store.protocol", "imaps");
	    System.setProperty("jakarta.mail.streamprovider", "jakarta.mail.util.DefaultStreamProvider");

	    Session session = Session.getInstance(props, null);
	    Store store = session.getStore();
	    store.connect(
	        ConfigManager.getInstance().getStringProperty("imap.server"),
	        ConfigManager.getInstance().getIntProperty("imap.port"),
	        username, password
	    );

	    Folder f = store.getFolder(folder);
	    f.open(Folder.READ_ONLY);

	    Message[] messages = f.getMessages();

	    if (index < 0 || index >= messages.length) {
	        throw new IndexOutOfBoundsException("No message found with such index");
	    }

	    Message message = messages[index];
	    String from = message.getFrom() != null ? message.getFrom()[0].toString() : "Desconocido";
	    from = MimeUtility.decodeText(from);
	    List<String> toList = new java.util.ArrayList<>();
	    if (message.getAllRecipients() != null) {
	        for (var address : message.getAllRecipients()) {
	            toList.add(address.toString());
	        }
	    }

	    String subject = message.getSubject();
	    String content = extractContent(message);
	    LocalDateTime date = message.getSentDate() != null
	            ? message.getSentDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
	            : LocalDateTime.now();

	    List<Attachment> attachments = extractAttachments(message);

	    f.close(false);
	    store.close();

	    return new Mail(from, toList, subject, content, date, attachments);
	}

	private static String extractContent(Message message) throws Exception {
	    Object content = message.getContent();
	    if (content instanceof String) {
	        return (String) content;
	    } else if (content instanceof Multipart) {
	        Multipart multipart = (Multipart) content;
	        for (int i = 0; i < multipart.getCount(); i++) {
	            BodyPart part = multipart.getBodyPart(i);
	            if (part.isMimeType("text/plain")) {
	                return part.getContent().toString();
	            } else if (part.isMimeType("text/html")) {
	                return part.getContent().toString();
	            }
	        }
	    }
	    return "";
	}
	
	private static List<Attachment> extractAttachments(Message message) throws Exception {
	    List<Attachment> attachments = new java.util.ArrayList<>();

	    if (message.getContent() instanceof Multipart multipart) {
	        for (int i = 0; i < multipart.getCount(); i++) {
	            BodyPart part = multipart.getBodyPart(i);

	            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) && part.getFileName() != null) {
	                String filename = part.getFileName();
	                String mimeType = part.getContentType().split(";")[0];

	                try (var inputStream = part.getInputStream()) {
	                    byte[] data = inputStream.readAllBytes();
	                    attachments.add(new Attachment(filename, mimeType, data));
	                }
	            }
	        }
	    }

	    return attachments;
	}

}
