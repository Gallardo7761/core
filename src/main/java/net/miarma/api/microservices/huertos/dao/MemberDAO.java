package net.miarma.api.microservices.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.MemberEntity;

import java.util.List;
import java.util.Map;

public class MemberDAO implements DataAccessObject<MemberEntity> {
	
	private final DatabaseManager db;
	
	public MemberDAO(Pool pool) {
		this.db = DatabaseManager.getInstance(pool);
	}

	@Override
	public Future<List<MemberEntity>> getAll() {
		return getAll(new QueryParams(Map.of(), new QueryFilters()));
	}
	
	public Future<List<MemberEntity>> getAll(QueryParams params) {
		Promise<List<MemberEntity>> promise = Promise.promise();
		String query = QueryBuilder
				.select(MemberEntity.class)
				.where(params.getFilters())
				.orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
				.limit(params.getQueryFilters().getLimit())
				.offset(params.getQueryFilters().getOffset())
				.build();
		db.execute(query, MemberEntity.class,
			list -> promise.complete(list.isEmpty() ? List.of() : list),
			promise::fail
		);
		return promise.future();
	}

	@Override
	public Future<MemberEntity> insert(MemberEntity user) {
		throw new UnsupportedOperationException("Insert not supported on view-based DAO");
	}

	@Override
	public Future<MemberEntity> update(MemberEntity user) {
		throw new UnsupportedOperationException("Update not supported on view-based DAO");
	}

	@Override
	public Future<MemberEntity> delete(Integer id) {
		throw new UnsupportedOperationException("Delete not supported on view-based DAO");
	}

	@Override
	public Future<MemberEntity> deleteDoubleId(Integer id1, Integer id2) {
		throw new UnsupportedOperationException("Delete not supported on view-based DAO");
	}

}
