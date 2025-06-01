package net.miarma.api.microservices.huertos.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.HuertosAnnouncePriority;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.time.LocalDateTime;

@Table("huertos_announces")
public class AnnounceEntity extends AbstractEntity {
	private Integer announce_id;
	private String body;
	private HuertosAnnouncePriority priority;
	private Integer published_by;
	private LocalDateTime created_at;
	
	public AnnounceEntity() {
		super();
	}
	
	public AnnounceEntity(Row row) {
		super(row);
	}
	
	public Integer getAnnounce_id() {
		return announce_id;
	}
	public void setAnnounce_id(Integer announce_id) {
		this.announce_id = announce_id;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public HuertosAnnouncePriority getPriority() {
		return priority;
	}
	public void setPriority(HuertosAnnouncePriority priority) {
		this.priority = priority;
	}
	public Integer getPublished_by() {
		return published_by;
	}
	public void setPublished_by(Integer published_by) {
		this.published_by = published_by;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	
	
}
