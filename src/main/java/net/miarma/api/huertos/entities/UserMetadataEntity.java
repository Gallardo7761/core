package net.miarma.api.huertos.entities;

import net.miarma.api.common.Table;
import net.miarma.api.common.Constants.HuertosUserRole;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.db.AbstractEntity;

@Table("huertos_user_metadata")
public class UserMetadataEntity extends AbstractEntity {
	private Integer user_id;
	private Integer member_number;
	private Integer plot_number;
	private String dni;
	private Integer phone;
	private Long created_at;
	private Long assigned_at;
	private Long deactivated_at;
	private String notes;
	private HuertosUserType type;
	private HuertosUserStatus status;
	private HuertosUserRole role;
	
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public Integer getMember_number() {
		return member_number;
	}
	public void setMember_number(Integer member_number) {
		this.member_number = member_number;
	}
	public Integer getPlot_number() {
		return plot_number;
	}
	public void setPlot_number(Integer plot_number) {
		this.plot_number = plot_number;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public Integer getPhone() {
		return phone;
	}
	public void setPhone(Integer phone) {
		this.phone = phone;
	}
	public Long getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Long created_at) {
		this.created_at = created_at;
	}
	public Long getAssigned_at() {
		return assigned_at;
	}
	public void setAssigned_at(Long assigned_at) {
		this.assigned_at = assigned_at;
	}
	public Long getDeactivated_at() {
		return deactivated_at;
	}
	public void setDeactivated_at(Long deactivated_at) {
		this.deactivated_at = deactivated_at;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public HuertosUserType getType() {
		return type;
	}
	public void setType(HuertosUserType type) {
		this.type = type;
	}
	public HuertosUserStatus getStatus() {
		return status;
	}
	public void setStatus(HuertosUserStatus status) {
		this.status = status;
	}
	public HuertosUserRole getRole() {
		return role;
	}
	public void setRole(HuertosUserRole role) {
		this.role = role;
	}
	
	
	
}
