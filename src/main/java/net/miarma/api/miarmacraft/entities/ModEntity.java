package net.miarma.api.miarmacraft.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.MMCModStatus;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.time.LocalDateTime;

@Table("miarmacraft_mods")
public class ModEntity extends AbstractEntity {
	private Integer mod_id;
	private String name;
	private String url;
	private MMCModStatus status;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;
	
	public ModEntity() {
		super();
	}
	
	public ModEntity(Row row) {
		super(row);
	}
	
	public Integer getMod_id() {
		return mod_id;
	}
	public void setMod_id(Integer mod_id) {
		this.mod_id = mod_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public MMCModStatus getStatus() {
		return status;
	}
	public void setStatus(MMCModStatus status) {
		this.status = status;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	public LocalDateTime getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(LocalDateTime updated_at) {
		this.updated_at = updated_at;
	}
	
	
}
