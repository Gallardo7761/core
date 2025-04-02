package net.miarma.api;

import java.time.LocalDateTime;

import net.miarma.api.common.VertxJacksonConfig;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.core.entities.UserEntity;

public class Test {
	public static void main(String[] args) {
		
		UserEntity user = new UserEntity();
		user.setUser_id(1);
        user.setUser_name("gallardo7761");
        user.setEmail("jomaamga@outlook.es");
        user.setDisplay_name("Jose");
        user.setPassword("$2a$12$.Vfpjx5YLXqnYwr5XUDRIOFVwm6fPNwZmn0cmRdXDArAeM3EhmwkO");
        user.setGlobal_status(CoreUserGlobalStatus.ACTIVE);
        user.setRole(CoreUserRole.ADMIN);
        user.setCreated_at(LocalDateTime.parse("2025-04-02T20:10:26"));
        user.setUpdated_at(LocalDateTime.parse("2025-04-02T20:18:24"));
		
        VertxJacksonConfig.configure();
        
		System.out.println(user.encode());
	}
}
