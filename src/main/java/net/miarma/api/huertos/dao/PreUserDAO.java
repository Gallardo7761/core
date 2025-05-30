package net.miarma.api.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.entities.PreUserEntity;

import java.util.List;
import java.util.Map;

public class PreUserDAO implements DataAccessObject<PreUserEntity> {

    private final DatabaseManager db;

    public PreUserDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<PreUserEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }
    
    public Future<List<PreUserEntity>> getAll(QueryParams params) {
        Promise<List<PreUserEntity>> promise = Promise.promise();
        String query = QueryBuilder
        		.select(PreUserEntity.class)
				.where(params.getFilters())
				.orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
				.limit(params.getQueryFilters().getLimit())
				.offset(params.getQueryFilters().getOffset())
				.build();

        db.execute(query, PreUserEntity.class,
            list -> promise.complete(list.isEmpty() ? List.of() : list),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<PreUserEntity> insert(PreUserEntity preUser) {
        Promise<PreUserEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(preUser).build();

        db.execute(query, PreUserEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<PreUserEntity> update(PreUserEntity preUser) {
        Promise<PreUserEntity> promise = Promise.promise();
        String query = QueryBuilder.update(preUser).build();

        db.executeOne(query, PreUserEntity.class,
            _ -> promise.complete(preUser),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<PreUserEntity> delete(Integer id) {
        Promise<PreUserEntity> promise = Promise.promise();
        PreUserEntity preUser = new PreUserEntity();
        preUser.setPre_user_id(id);

        String query = QueryBuilder.delete(preUser).build();

        db.executeOne(query, PreUserEntity.class,
            _ -> promise.complete(preUser),
            promise::fail
        );

        return promise.future();
    }
}
