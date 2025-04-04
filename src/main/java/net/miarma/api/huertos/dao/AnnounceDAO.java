package net.miarma.api.huertos.dao;

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
import net.miarma.api.huertos.entities.AnnounceEntity;

public class AnnounceDAO implements DataAccessObject<AnnounceEntity> {

    private final DatabaseManager db;

    public AnnounceDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<AnnounceEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }
    
    public Future<List<AnnounceEntity>> getAll(QueryParams params) {
        Promise<List<AnnounceEntity>> promise = Promise.promise();
        String query = QueryBuilder
        		.select(AnnounceEntity.class)
        		.where(params.getFilters())
				.orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
				.limit(params.getQueryFilters().getLimit())
				.offset(params.getQueryFilters().getOffset())
				.build();

        db.execute(query, AnnounceEntity.class,
            list -> promise.complete(list.isEmpty() ? List.of() : list),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<AnnounceEntity> insert(AnnounceEntity announce) {
        Promise<AnnounceEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(announce).build();

        db.execute(query, AnnounceEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<AnnounceEntity> update(AnnounceEntity announce) {
        Promise<AnnounceEntity> promise = Promise.promise();
        String query = QueryBuilder.update(announce).build();

        db.execute(query, AnnounceEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<AnnounceEntity> delete(Integer id) {
        Promise<AnnounceEntity> promise = Promise.promise();
        AnnounceEntity announce = new AnnounceEntity();
        announce.setAnnounce_id(id);

        String query = QueryBuilder.delete(announce).build();

        db.execute(query, AnnounceEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }
}
