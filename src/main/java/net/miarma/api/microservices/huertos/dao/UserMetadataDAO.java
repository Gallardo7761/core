package net.miarma.api.microservices.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.UserMetadataEntity;

import java.util.List;
import java.util.Map;

public class UserMetadataDAO implements DataAccessObject<UserMetadataEntity> {
	
	private final DatabaseManager db;
	
	public UserMetadataDAO(Pool pool) {
		this.db = DatabaseManager.getInstance(pool);
	}

	@Override
	public Future<List<UserMetadataEntity>> getAll() {
		return getAll(new QueryParams(Map.of(), new QueryFilters()));
	}
	
	public Future<List<UserMetadataEntity>> getAll(QueryParams params) {
		Promise<List<UserMetadataEntity>> promise = Promise.promise();
		String query = QueryBuilder
				.select(UserMetadataEntity.class)
				.where(params.getFilters())
				.orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
				.limit(params.getQueryFilters().getLimit())
				.offset(params.getQueryFilters().getOffset())
				.build();

		db.execute(query, UserMetadataEntity.class,
			list -> promise.complete(list.isEmpty() ? List.of() : list),
			promise::fail
		);

		return promise.future();
	}

	@Override
	public Future<UserMetadataEntity> insert(UserMetadataEntity user) {
		Promise<UserMetadataEntity> promise = Promise.promise();
		String query = QueryBuilder.insert(user).build();

		db.executeOne(query, UserMetadataEntity.class,
            result -> promise.complete(result),
            promise::fail
        );

		return promise.future();
	}

	@Override
	public Future<UserMetadataEntity> update(UserMetadataEntity user) {
		Promise<UserMetadataEntity> promise = Promise.promise();
		String query = QueryBuilder.update(user).build();

		db.executeOne(query, UserMetadataEntity.class,
            _ -> promise.complete(user),
            promise::fail
        );

		return promise.future();
	}
	
	public Future<UserMetadataEntity> updateWithNulls(UserMetadataEntity user) {
		Promise<UserMetadataEntity> promise = Promise.promise();
		String query = QueryBuilder.updateWithNulls(user).build();

		db.executeOne(query, UserMetadataEntity.class,
            _ -> promise.complete(user),
            promise::fail
        );

		return promise.future();
	}

	@Override
	public Future<UserMetadataEntity> delete(Integer id) {
		Promise<UserMetadataEntity> promise = Promise.promise();
		UserMetadataEntity user = new UserMetadataEntity();
		user.setUser_id(id);

		String query = QueryBuilder.delete(user).build();

		db.executeOne(query, UserMetadataEntity.class,
            _ -> promise.complete(user),
            promise::fail
        );

		return promise.future();
	}

	@Override
	public Future<UserMetadataEntity> deleteDoubleId(Integer id1, Integer id2) {
		throw new UnsupportedOperationException("This method is not supported for UserMetadataDAO");
	}
}
