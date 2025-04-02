package net.miarma.api;

import java.time.LocalDateTime;

import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.Constants.HuertosUserRole;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.huertos.entities.MemberEntity;
import net.miarma.api.huertos.entities.UserMetadataEntity;

public class Test {
	public static void main(String[] args) {
		
		UserEntity user = new UserEntity();
        user.setUser_id(1);
        user.setUser_name("josemi");
        user.setEmail("jose@example.com");
        user.setDisplay_name("Jose");
        user.setPassword("supersecreta");
        user.setAvatar("https://miarma.link/avatar.png");
        user.setGlobal_status(CoreUserGlobalStatus.ACTIVE);
        user.setRole(CoreUserRole.USER);

        UserMetadataEntity metadata = new UserMetadataEntity();
        metadata.setUser_id(1);
        metadata.setMember_number(100);
        metadata.setPlot_number(12);
        metadata.setDni("12345678Z");
        metadata.setPhone(666777888);
        metadata.setCreated_at(LocalDateTime.now());
        metadata.setAssigned_at(null);
        metadata.setDeactivated_at(null);
        metadata.setNotes("Es un crack");
        metadata.setType(HuertosUserType.MEMBER);
        metadata.setStatus(HuertosUserStatus.ACTIVE);
        metadata.setRole(HuertosUserRole.USER);
        
        MemberEntity member = new MemberEntity(user, metadata);

        String json = Constants.GSON.toJson(member);
        System.out.println(json);
	}
}
