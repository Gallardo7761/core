package net.miarma.api.microservices.huertosdecine.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.time.LocalDateTime;

@Table("cine_user_metadata")
public class UserMetadataEntity extends AbstractEntity {
    private Integer user_id;
    private Constants.CineUserStatus status;
    private Constants.CineUserRole role;
    private LocalDateTime created_at;

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

    public Constants.CineUserStatus getStatus() {
        return status;
    }

    public void setStatus(Constants.CineUserStatus status) {
        this.status = status;
    }

    public Constants.CineUserRole getRole() {
        return role;
    }

    public void setRole(Constants.CineUserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public static UserMetadataEntity fromViewerEntity(ViewerEntity viewer) {
        UserMetadataEntity metadata = new UserMetadataEntity();
        metadata.setUser_id(viewer.getUser_id());
        metadata.setStatus(viewer.getStatus());
        metadata.setRole(viewer.getRole());
        metadata.setCreated_at(viewer.getCreated_at());
        return metadata;
    }
}
