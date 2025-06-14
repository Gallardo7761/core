package net.miarma.api.microservices.huertosdecine.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertosdecine.entities.ViewerEntity;

import java.util.List;
import java.util.Map;

public class ViewerDAO implements DataAccessObject<ViewerEntity, Integer> {

    private final DatabaseManager db;

    public ViewerDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<ViewerEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<ViewerEntity> getById(Integer id) {
        Promise<ViewerEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(ViewerEntity.class)
                .where(Map.of("user_id", id))
                .build();

        db.executeOne(query, ViewerEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<ViewerEntity>> getAll(QueryParams params) {
        Promise<List<ViewerEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(ViewerEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();

        db.execute(query, ViewerEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<ViewerEntity> insert(ViewerEntity viewerEntity) {
        throw new UnsupportedOperationException("Insert not supported on view-based DAO");
    }

    @Override
    public Future<ViewerEntity> upsert(ViewerEntity viewer, String... conflictKeys) {
        throw new UnsupportedOperationException("Upsert not supported on view-based DAO");
    }

    @Override
    public Future<ViewerEntity> update(ViewerEntity viewerEntity) {
        throw new UnsupportedOperationException("Update not supported on view-based DAO");
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        throw new UnsupportedOperationException("Delete not supported on view-based DAO");
    }

    @Override
    public Future<Boolean> exists(Integer integer) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(ViewerEntity.class)
                .where(Map.of("user_id", integer))
                .build();

        db.executeOne(query, ViewerEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
