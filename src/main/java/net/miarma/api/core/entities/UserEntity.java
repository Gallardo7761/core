package net.miarma.api.core.entities;

import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.annotations.APIDontReturn;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;
import net.miarma.api.huertos.entities.MemberEntity;

@Table("users")
public class UserEntity extends AbstractEntity {
    private Integer user_id;
    private String user_name;
    private String email;
    private String display_name;
    @APIDontReturn
    private String password;
    private String avatar;
    private CoreUserGlobalStatus global_status;
    private CoreUserRole role;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public UserEntity() { }
    public UserEntity(Row row) { super(row); }
    
    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public String getUser_name() { return user_name; }
    public void setUser_name(String user_name) { this.user_name = user_name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplay_name() { return display_name; }
    public void setDisplay_name(String display_name) { this.display_name = display_name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public CoreUserGlobalStatus getGlobal_status() { return global_status; }
    public void setGlobal_status(CoreUserGlobalStatus global_status) { this.global_status = global_status; }
    public CoreUserRole getRole() { return role; }
    public void setRole(CoreUserRole role) { this.role = role; }
    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
    public LocalDateTime getUpdated_at() { return updated_at; }
    public void setUpdated_at(LocalDateTime updated_at) { this.updated_at = updated_at; }
    
    public static UserEntity fromMemberEntity(MemberEntity member) {
    	UserEntity user = new UserEntity();
    	user.setUser_id(member.getUser_id());
    	user.setUser_name(member.getUser_name());
    	user.setDisplay_name(member.getDisplay_name());
    	user.setEmail(member.getEmail());
    	user.setPassword(member.getPassword());
    	user.setAvatar(member.getAvatar());
    	user.setGlobal_status(member.getGlobal_status());
    	user.setRole(member.getGlobal_role());
    	return user;
    }

    
}