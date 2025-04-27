package net.miarma.api.miarmacraft.dao;

import java.util.List;
import java.util.Map;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.miarmacraft.entities.PlayerEntity;

public class PlayerDAO implements DataAccessObject<PlayerEntity> {

	private final DatabaseManager db;
	
	public PlayerDAO(Pool pool) {
		this.db = DatabaseManager.getInstance(pool);
	}
	
	@Override
	public Future<List<PlayerEntity>> getAll() {
		return getAll(new QueryParams(Map.of(), new QueryFilters()));
	}
	
	public Future<List<PlayerEntity>> getAll(QueryParams params) {
		Promise<List<PlayerEntity>> promise = Promise.promise();
		
		String query = QueryBuilder
			.select(PlayerEntity.class)
			.where(params.getFilters())
			.orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
			.limit(params.getQueryFilters().getLimit())
			.offset(params.getQueryFilters().getOffset())
			.build();
		
		db.execute(query, PlayerEntity.class,
			list -> promise.complete(list.isEmpty() ? List.of() : list),
			promise::fail
		);
		
		return promise.future();
	}

	@Override
	public Future<PlayerEntity> insert(PlayerEntity t) {
		throw new UnsupportedOperationException("Insert not supported on view-based DAO");
	}

	@Override
	public Future<PlayerEntity> update(PlayerEntity t) {
		throw new UnsupportedOperationException("Insert not supported on view-based DAO");
	}

	@Override
	public Future<PlayerEntity> delete(Integer id) {
		throw new UnsupportedOperationException("Insert not supported on view-based DAO");
	}

}
