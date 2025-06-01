package net.miarma.api.microservices.huertos.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.*;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.time.LocalDateTime;

@Table("v_requests_with_pre_users")
public class ViewRequestsWithPreUsers extends AbstractEntity {

    // huertos_requests
    public Integer request_id;
    public HuertosRequestType request_type;
    public HuertosRequestStatus request_status;
    public Integer requested_by;
    	// huertos_users
    	public String requested_by_name;
    public Integer target_user_id;
    public LocalDateTime request_created_at;

    // huertos_pre_users
    public Integer pre_user_id;
    public String pre_user_name;
    public String pre_display_name;
    public String pre_dni;
    public Integer pre_phone;
    public String pre_email;
    public String pre_address;
    public String pre_zip_code;
    public String pre_city;
    public Integer pre_member_number;
    public Integer pre_plot_number;
    public HuertosUserType pre_type;
    public HuertosUserStatus pre_status;
    public HuertosUserRole pre_role;
    public LocalDateTime pre_created_at;
    
    public ViewRequestsWithPreUsers() {
    	super();
    }
    
    public ViewRequestsWithPreUsers(Row row) {
    	super(row);
    }

	public Integer getRequest_id() {
		return request_id;
	}

	public void setRequest_id(Integer request_id) {
		this.request_id = request_id;
	}

	public HuertosRequestType getRequest_type() {
		return request_type;
	}

	public void setRequest_type(HuertosRequestType request_type) {
		this.request_type = request_type;
	}

	public HuertosRequestStatus getRequest_status() {
		return request_status;
	}

	public void setRequest_status(HuertosRequestStatus request_status) {
		this.request_status = request_status;
	}

	public Integer getRequested_by() {
		return requested_by;
	}

	public void setRequested_by(Integer requested_by) {
		this.requested_by = requested_by;
	}

	public String getRequested_by_name() {
		return requested_by_name;
	}

	public void setRequested_by_name(String requested_by_name) {
		this.requested_by_name = requested_by_name;
	}

	public Integer getTarget_user_id() {
		return target_user_id;
	}

	public void setTarget_user_id(Integer target_user_id) {
		this.target_user_id = target_user_id;
	}

	public LocalDateTime getRequest_created_at() {
		return request_created_at;
	}

	public void setRequest_created_at(LocalDateTime request_created_at) {
		this.request_created_at = request_created_at;
	}

	public Integer getPre_user_id() {
		return pre_user_id;
	}

	public void setPre_user_id(Integer pre_user_id) {
		this.pre_user_id = pre_user_id;
	}

	public String getPre_user_name() {
		return pre_user_name;
	}

	public void setPre_user_name(String pre_user_name) {
		this.pre_user_name = pre_user_name;
	}

	public String getPre_display_name() {
		return pre_display_name;
	}

	public void setPre_display_name(String pre_display_name) {
		this.pre_display_name = pre_display_name;
	}

	public String getPre_dni() {
		return pre_dni;
	}

	public void setPre_dni(String pre_dni) {
		this.pre_dni = pre_dni;
	}

	public Integer getPre_phone() {
		return pre_phone;
	}

	public void setPre_phone(Integer pre_phone) {
		this.pre_phone = pre_phone;
	}

	public String getPre_email() {
		return pre_email;
	}

	public void setPre_email(String pre_email) {
		this.pre_email = pre_email;
	}

	public String getPre_address() {
		return pre_address;
	}

	public void setPre_address(String pre_address) {
		this.pre_address = pre_address;
	}

	public String getPre_zip_code() {
		return pre_zip_code;
	}

	public void setPre_zip_code(String pre_zip_code) {
		this.pre_zip_code = pre_zip_code;
	}

	public String getPre_city() {
		return pre_city;
	}

	public void setPre_city(String pre_city) {
		this.pre_city = pre_city;
	}

	public Integer getPre_member_number() {
		return pre_member_number;
	}

	public void setPre_member_number(Integer pre_member_number) {
		this.pre_member_number = pre_member_number;
	}

	public Integer getPre_plot_number() {
		return pre_plot_number;
	}

	public void setPre_plot_number(Integer pre_plot_number) {
		this.pre_plot_number = pre_plot_number;
	}

	public HuertosUserType getPre_type() {
		return pre_type;
	}

	public void setPre_type(HuertosUserType pre_type) {
		this.pre_type = pre_type;
	}

	public HuertosUserStatus getPre_status() {
		return pre_status;
	}

	public void setPre_status(HuertosUserStatus pre_status) {
		this.pre_status = pre_status;
	}

	public HuertosUserRole getPre_role() {
		return pre_role;
	}

	public void setPre_role(HuertosUserRole pre_role) {
		this.pre_role = pre_role;
	}

	public LocalDateTime getPre_created_at() {
		return pre_created_at;
	}

	public void setPre_created_at(LocalDateTime pre_created_at) {
		this.pre_created_at = pre_created_at;
	}   
    
}
