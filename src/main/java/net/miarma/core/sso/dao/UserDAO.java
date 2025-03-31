package net.miarma.core.sso.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.db.DatabaseManager;
import net.miarma.core.common.db.QueryBuilder;
import net.miarma.core.sso.entities.UserEntity;

public class UserDAO {

    private final DatabaseManager db;

    public UserDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }
    
    public void getAll(Handler<AsyncResult<List<UserEntity>>> handler) {
    	String query = QueryBuilder
    			.select(UserEntity.class)
    			.build();
		
    	db.execute(query, UserEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list)),
			ex -> handler.handle(Future.failedFuture(ex))
		);
    }
	
	public void insert(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
		String query = QueryBuilder
				.insert(user)
				.build();
		
		db.execute(query, UserEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}
	
	public void update(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
		String query = QueryBuilder
				.update(user)
				.build();
		
		db.execute(query, UserEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}
	
	public void delete(Integer id, Handler<AsyncResult<UserEntity>> handler) {
		UserEntity user = new UserEntity();
		user.setUser_id(id);
		
		String query = QueryBuilder
				.delete(user)
				.build();
		
		db.execute(query, UserEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}
	
}