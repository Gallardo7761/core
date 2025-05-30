package net.miarma.api.miarmacraft.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.Constants.MMCUserRole;
import net.miarma.api.common.Constants.MMCUserStatus;
import net.miarma.api.common.annotations.APIDontReturn;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;
import net.miarma.api.core.entities.UserEntity;

import java.time.LocalDateTime;

@Table("v_miarmacraft_players")
public class PlayerEntity extends AbstractEntity{
	private Integer user_id;
	private String user_name;
	private String email;
	private String display_name;
	@APIDontReturn
	private String password;
	private String avatar;
	private MMCUserRole role;	
	private MMCUserStatus status;
	private CoreUserGlobalStatus global_status;
	private CoreUserRole global_role;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;
	
	public PlayerEntity() {
		super();
	}
	
	public PlayerEntity(Row row) {
		super(row);
	}

	public PlayerEntity(UserEntity user, UserMetadataEntity userMetadata) {
		this.user_id = user.getUser_id();
		this.user_name = user.getUser_name();
		this.email = user.getEmail();
		this.display_name = user.getDisplay_name();
		this.password = user.getPassword();
		this.avatar = user.getAvatar();
		this.role = userMetadata.getRole();
		this.status = userMetadata.getStatus();
		this.global_status = user.getGlobal_status();
		this.global_role = user.getRole();
		this.created_at = user.getCreated_at();
		this.updated_at = user.getUpdated_at();
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public MMCUserRole getRole() {
		return role;
	}

	public void setRole(MMCUserRole role) {
		this.role = role;
	}

	public MMCUserStatus getStatus() {
		return status;
	}

	public void setStatus(MMCUserStatus status) {
		this.status = status;
	}

	public CoreUserGlobalStatus getGlobal_status() {
		return global_status;
	}

	public void setGlobal_status(CoreUserGlobalStatus global_status) {
		this.global_status = global_status;
	}

	public CoreUserRole getGlobal_role() {
		return global_role;
	}

	public void setGlobal_role(CoreUserRole global_role) {
		this.global_role = global_role;
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
