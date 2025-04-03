package net.miarma.api.huertos.entities;

import io.vertx.core.json.JsonObject;
import net.miarma.api.core.entities.UserEntity;

public class MemberEntity {
	
	private UserEntity user;
	private UserMetadataEntity metadata;

	public MemberEntity(UserEntity user, UserMetadataEntity metadata) {
		this.user = user;
		this.metadata = metadata;
	}
	
	public UserEntity getUser() {
		return user;
	}
	
	public UserMetadataEntity getMetadata() {
		return metadata;
	}
	
	public void setUser(UserEntity user) {
		this.user = user;
	}
	
	public void setMetadata(UserMetadataEntity metadata) {
		this.metadata = metadata;
	}
	
	public String encode() {
	    JsonObject json = new JsonObject()
	        .put("user", new JsonObject(user.encode()))
	        .put("metadata", new JsonObject(metadata.encode()));
	    return json.encode();
	}

	
}
