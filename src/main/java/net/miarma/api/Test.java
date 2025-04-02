package net.miarma.api;

import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.miarma.api.common.LocalDateTimeAdapter;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.core.entities.UserEntity;

public class Test {
	public static void main(String[] args) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
		
		UserEntity user = new UserEntity();
        user.setUser_id(1);
        user.setUser_name("miarma");
        user.setEmail("miarma@sevilla.es");
        user.setDisplay_name("El Miarma");
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());
		
        String query = QueryBuilder
    			.select(UserEntity.class)
    			.build();
        
		System.out.println(query);
	}
}
