package net.miarma.api.huertos.entities;

import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.HuertosRequestStatus;
import net.miarma.api.common.Constants.HuertosRequestType;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

@Table("huertos_requests")
public class RequestEntity extends AbstractEntity {
	private Integer request_id;
	private HuertosRequestType type;
	private HuertosRequestStatus status;
	private Integer requested_by;
	private Integer target_user_id;
	private LocalDateTime created_at;
	
	public RequestEntity() {
		super();
	}
	
	public RequestEntity(Row row) {
		super(row);
	}
	
	public Integer getRequest_id() {
		return request_id;
	}
	public void setRequest_id(Integer request_id) {
		this.request_id = request_id;
	}
	public HuertosRequestType getType() {
		return type;
	}
	public void setType(HuertosRequestType type) {
		this.type = type;
	}
	public HuertosRequestStatus getStatus() {
		return status;
	}
	public void setStatus(HuertosRequestStatus status) {
		this.status = status;
	}
	public Integer getRequested_by() {
		return requested_by;
	}
	public void setRequested_by(Integer requested_by) {
		this.requested_by = requested_by;
	}
	public Integer getTarget_user_id() {
		return target_user_id;
	}
	public void setTarget_user_id(Integer target_user_id) {
		this.target_user_id = target_user_id;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	
		
}
