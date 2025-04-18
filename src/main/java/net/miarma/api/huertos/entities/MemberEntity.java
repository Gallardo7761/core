package net.miarma.api.huertos.entities;

import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.Constants.HuertosUserRole;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.annotations.APIDontReturn;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;
import net.miarma.api.core.entities.UserEntity;

@Table("v_huertos_members")
public class MemberEntity extends AbstractEntity {

    private Integer user_id;
    private Integer member_number;
    private Integer plot_number;
    private String display_name;
    private String dni;
    private Integer phone;
    private String email;
    private String user_name;
    @APIDontReturn
    private String password;
    private String avatar;
    private LocalDateTime created_at;
    private LocalDateTime assigned_at;
    private LocalDateTime deactivated_at;
    private String notes;
    private HuertosUserType type;
    private HuertosUserStatus status;
    private HuertosUserRole role;
    private CoreUserGlobalStatus global_status;
    private CoreUserRole global_role;

    public MemberEntity() {
    	super();
    }
    
    public MemberEntity(Row row) {
    	super(row);
    }

    public MemberEntity(UserEntity user, UserMetadataEntity metadata) {
        this.user_id = user.getUser_id();
        this.member_number = metadata.getMember_number();
        this.plot_number = metadata.getPlot_number();
        this.display_name = user.getDisplay_name();
        this.dni = metadata.getDni();
        this.phone = metadata.getPhone();
        this.email = user.getEmail();
        this.user_name = user.getUser_name();
        this.password = user.getPassword();
        this.avatar = user.getAvatar();
        this.created_at = metadata.getCreated_at();
        this.assigned_at = metadata.getAssigned_at();
        this.deactivated_at = metadata.getDeactivated_at();
        this.notes = metadata.getNotes();
        this.type = metadata.getType();
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getAssigned_at() {
        return assigned_at;
    }

    public void setAssigned_at(LocalDateTime assigned_at) {
        this.assigned_at = assigned_at;
    }

    public LocalDateTime getDeactivated_at() {
        return deactivated_at;
    }

    public void setDeactivated_at(LocalDateTime deactivated_at) {
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
    
    public static MemberEntity fromPreUser(PreUserEntity preUser) {
		MemberEntity member = new MemberEntity();
		member.setMember_number(preUser.getMember_number());
		member.setPlot_number(preUser.getPlot_number());
		member.setDisplay_name(preUser.getDisplay_name());
		member.setDni(preUser.getDni());
		member.setPhone(preUser.getPhone());
		member.setEmail(preUser.getEmail());
		member.setUser_name(preUser.getUser_name());
		member.setCreated_at(preUser.getCreated_at());
		member.setType(preUser.getType());
		member.setStatus(preUser.getStatus());
		member.setRole(HuertosUserRole.USER);
		member.setGlobal_status(CoreUserGlobalStatus.ACTIVE);
		member.setGlobal_role(CoreUserRole.USER);
		return member;
	}
}
