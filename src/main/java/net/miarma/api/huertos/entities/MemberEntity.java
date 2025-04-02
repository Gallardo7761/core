package net.miarma.api.huertos.entities;

import net.miarma.api.core.entities.UserEntity;

public class MemberEntity {
	
	private UserEntity user;
	private UserMetadataEntity metadata;

	public MemberEntity(UserEntity user, UserMetadataEntity userMetadata) {
		this.user = user;
		this.metadata = userMetadata;
	}
	
	public UserEntity getUser() {
		return user;
	}
	
	public UserMetadataEntity getUserMetadata() {
		return metadata;
	}
	
	public void setUser(UserEntity user) {
		this.user = user;
	}
	
	public void setUserMetadata(UserMetadataEntity userMetadata) {
		this.metadata = userMetadata;
	}
	
}
