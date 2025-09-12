package net.miarma.api.microservices.mpaste.entities;

import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

@Table("mpaste_pastes")
public class PasteEntity extends AbstractEntity {
	private Long paste_id;
	private String paste_key;
	private String content;
	private String syntax;
	private LocalDateTime created_at;
	private LocalDateTime expires_at;
	private Integer views;
	private Boolean burn_after;
	private Boolean is_private;
	private Integer owner_id;
	
	public PasteEntity() {
        super();
    }

    public PasteEntity(Row row) {
        super(row);
    }

	public Long getPaste_id() {
		return paste_id;
	}

	public void setPaste_id(Long paste_id) {
		this.paste_id = paste_id;
	}

	public String getPaste_key() {
		return paste_key;
	}

	public void setPaste_key(String paste_key) {
		this.paste_key = paste_key;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public LocalDateTime getExpires_at() {
		return expires_at;
	}

	public void setExpires_at(LocalDateTime expires_at) {
		this.expires_at = expires_at;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public Boolean getBurn_after() {
		return burn_after;
	}

	public void setBurn_after(Boolean burn_after) {
		this.burn_after = burn_after;
	}

	public Boolean getIs_private() {
		return is_private;
	}

	public void setIs_private(Boolean is_private) {
		this.is_private = is_private;
	}

	public Integer getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(Integer owner_id) {
		this.owner_id = owner_id;
	}
}
