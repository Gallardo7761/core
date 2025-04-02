package net.miarma.api.core.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.core.entities.UserEntity;

public class UserDAO implements DataAccessObject<UserEntity> {

    private final DatabaseManager db;

    public UserDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }
    
    @Override
    public void getAll(Handler<AsyncResult<List<UserEntity>>> handler) {
    	String query = QueryBuilder
    			.select(UserEntity.class)
    			.build();
		
    	db.execute(query, UserEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
			ex -> handler.handle(Future.failedFuture(ex))
		);
    }
	
    @Override
	public void insert(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
		String query = QueryBuilder
				.insert(user)
				.build();
		
		db.execute(query, UserEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}
	
    @Override
	public void update(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
		String query = QueryBuilder
				.update(user)
				.build();
		
		db.execute(query, UserEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}
	
    @Override
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