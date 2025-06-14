package net.miarma.api.microservices.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.PreUserEntity;

import java.util.List;
import java.util.Map;

public class PreUserDAO implements DataAccessObject<PreUserEntity, Integer> {

    private final DatabaseManager db;

    public PreUserDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<PreUserEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<PreUserEntity> getById(Integer id) {
        Promise<PreUserEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(PreUserEntity.class)
                .where(Map.of("pre_user_id", id))
                .build();

        db.executeOne(query, PreUserEntity.class,
                promise::complete,
            promise::fail
        );

        return promise.future();
    }

    public Future<PreUserEntity> getByRequestId(Integer requestId) {
        Promise<PreUserEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(PreUserEntity.class)
                .where(Map.of("request_id", requestId))
                .build();

        db.executeOne(query, PreUserEntity.class,
                promise::complete,
            promise::fail
        );

        return promise.future();
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
            list -> promise.complete(list.isEmpty() ? null : list.getFirst()),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<PreUserEntity> upsert(PreUserEntity preUserEntity, String... conflictKeys) {
        Promise<PreUserEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(preUserEntity, conflictKeys).build();

        db.execute(query, PreUserEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.getFirst()),
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
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        PreUserEntity preUser = new PreUserEntity();
        preUser.setPre_user_id(id);

        String query = QueryBuilder.delete(preUser).build();

        db.executeOne(query, PreUserEntity.class,
            result -> promise.complete(result != null),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(PreUserEntity.class)
                .where(Map.of("pre_user_id", id))
                .build();

        db.execute(query, PreUserEntity.class,
            list -> promise.complete(!list.isEmpty()),
            promise::fail
        );

        return promise.future();
    }
}
