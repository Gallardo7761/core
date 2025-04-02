package net.miarma.api.huertos.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.IDataAccessObject;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.huertos.entities.UserMetadataEntity;

public class UserMetadataDAO implements IDataAccessObject<UserMetadataEntity> {
	
	private final DatabaseManager db;
	
	public UserMetadataDAO(Pool pool) {
		this.db = DatabaseManager.getInstance(pool);
	}

	@Override
	public void getAll(Handler<AsyncResult<List<UserMetadataEntity>>> handler) {
		String query = QueryBuilder
				.select(UserMetadataEntity.class)
				.build();
		
		db.execute(query, UserMetadataEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}

	@Override
	public void insert(UserMetadataEntity user, Handler<AsyncResult<UserMetadataEntity>> handler) {
		String query = QueryBuilder
				.insert(user)
				.build();
		
		db.execute(query, UserMetadataEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}

	@Override
	public void update(UserMetadataEntity user, Handler<AsyncResult<UserMetadataEntity>> handler) {
		String query = QueryBuilder
				.update(user)
				.build();
		
		db.execute(query, UserMetadataEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}

	@Override
	public void delete(Integer id, Handler<AsyncResult<UserMetadataEntity>> handler) {
		String query = QueryBuilder
				.delete(UserMetadataEntity.class)
				.build();	
		
		db.execute(query, UserMetadataEntity.class,
			list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
			ex -> handler.handle(Future.failedFuture(ex))
		);
	}
	
	
}
