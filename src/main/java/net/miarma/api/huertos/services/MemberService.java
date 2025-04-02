package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.core.dao.UserDAO;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.huertos.dao.UserMetadataDAO;
import net.miarma.api.huertos.entities.MemberEntity;
import net.miarma.api.huertos.entities.UserMetadataEntity;

public class MemberService {
	
	private final UserDAO userDAO;
	private final UserMetadataDAO userMetadataDAO;
	
	public MemberService(Pool pool) {
		this.userDAO = new UserDAO(pool);
		this.userMetadataDAO = new UserMetadataDAO(pool);
	}
	
	public void getAll(Handler<AsyncResult<List<MemberEntity>>> handler) {
		//TODO: REFACTOR DAO's TO RETURN PROMISES INSTEAD OF USING HANDLER ARGS
		
		List<UserEntity> users = List.of();
		List<UserMetadataEntity> userMetadata = List.of();
	}
	
	
}
