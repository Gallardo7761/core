package net.miarma.api.microservices.huertosdecine.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants;
import net.miarma.api.common.annotations.APIDontReturn;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;
import net.miarma.api.microservices.core.entities.UserEntity;

import java.time.LocalDateTime;

@Table("v_cine_viewers")
public class ViewerEntity extends AbstractEntity {
    private Integer user_id;
    private String user_name;
    private String email;
    private String display_name;
    @APIDontReturn
    private String password;
    private String avatar;
    private Constants.CineUserStatus status;
    private Constants.CineUserRole role;
    private Constants.CoreUserGlobalStatus global_status;
    private Constants.CoreUserRole global_role;
    private LocalDateTime created_at;

    public ViewerEntity() {
        super();
    }

    public ViewerEntity(Row row) {
        super(row);
    }

    public ViewerEntity(UserEntity user, UserMetadataEntity metadata) {
        this.user_id = user.getUser_id();
        this.user_name = user.getUser_name();
        this.email = user.getEmail();
        this.display_name = user.getDisplay_name();
        this.password = user.getPassword();
        this.avatar = user.getAvatar();
        this.status = metadata.getStatus();
        this.role = metadata.getRole();
        this.global_status = user.getGlobal_status();
        this.global_role = user.getRole();
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

    public Constants.CoreUserGlobalStatus getGlobal_status() {
        return global_status;
    }

    public void setGlobal_status(Constants.CoreUserGlobalStatus global_status) {
        this.global_status = global_status;
    }

    public Constants.CoreUserRole getGlobal_role() {
        return global_role;
    }

    public void setGlobal_role(Constants.CoreUserRole global_role) {
        this.global_role = global_role;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
