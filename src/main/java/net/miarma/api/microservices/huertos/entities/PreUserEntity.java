package net.miarma.api.microservices.huertos.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.time.LocalDateTime;

@Table("huertos_pre_users")
public class PreUserEntity extends AbstractEntity {
	private Integer pre_user_id;
	private Integer request_id;
	private String user_name;
	private String display_name;
	private String dni;
	private Integer phone;
	private String email;
	private String password;
	private String address;
	private String zip_code;
	private String city;
	private Integer member_number;
	private Integer plot_number;
	private HuertosUserType type;
	private HuertosUserStatus status;
	private LocalDateTime created_at;
	
	
	public PreUserEntity() {
		super();
	}
	
	public PreUserEntity(Row row) {
		super(row);
	}
	
	public Integer getPre_user_id() {
		return pre_user_id;
	}
	public void setPre_user_id(Integer pre_user_id) {
		this.pre_user_id = pre_user_id;
	}
	public Integer getRequest_id() {
		return request_id;
	}
	public void setRequest_id(Integer request_id) {
		this.request_id = request_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZip_code() {
		return zip_code;
	}
	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
}
