package net.miarma.api.miarmacraft.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.MMCUserRole;
import net.miarma.api.common.Constants.MMCUserStatus;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

@Table("miarmacraft_players")
public class UserMetadataEntity extends AbstractEntity {
	private Integer user_id;
	private MMCUserRole role;
	private MMCUserStatus status;
	
	public UserMetadataEntity() {
		super();
	}
	
	public UserMetadataEntity(Row row) {
		super(row);
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
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
	
	public static UserMetadataEntity fromPlayerEntity(PlayerEntity player) {
		UserMetadataEntity userMetadata = new UserMetadataEntity();
		userMetadata.setUser_id(player.getUser_id());
		userMetadata.setRole(player.getRole());
		userMetadata.setStatus(player.getStatus());
		return userMetadata;
	}
}
