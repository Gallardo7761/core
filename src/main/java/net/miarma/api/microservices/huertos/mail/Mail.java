package net.miarma.api.microservices.huertos.mail;

import java.time.LocalDateTime;
import java.util.List;

public class Mail implements Comparable<Mail> {
	private String from;
	private List<String> to;
	private String subject;
	private String content;
	private LocalDateTime date;
	private List<Attachment> attachments;
	
	public Mail(String from, List<String> to, String subject, String content, LocalDateTime date,
			List<Attachment> attachments) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.date = date;
		this.attachments = attachments;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		return "MailEntity [from=" + from + ", to=" + to + ", subject=" + subject + ", content=" + content + ", date="
				+ date + ", attachments=" + attachments + "]";
	}

	@Override
	public int compareTo(Mail other) {
		return this.date.compareTo(other.date);
	}
	
}
