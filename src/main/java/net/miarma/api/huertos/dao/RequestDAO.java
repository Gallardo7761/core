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
import net.miarma.api.huertos.entities.RequestEntity;

public class RequestDAO implements DataAccessObject<RequestEntity> {

    private final DatabaseManager db;

    public RequestDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<RequestEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }
    
    public Future<List<RequestEntity>> getAll(QueryParams params) {
        Promise<List<RequestEntity>> promise = Promise.promise();
        String query = QueryBuilder
        		.select(RequestEntity.class)
        		.where(params.getFilters())
				.orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
				.limit(params.getQueryFilters().getLimit())
				.offset(params.getQueryFilters().getOffset())
				.build();

        db.execute(query, RequestEntity.class,
            list -> promise.complete(list.isEmpty() ? List.of() : list),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<RequestEntity> insert(RequestEntity request) {
        Promise<RequestEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(request).build();

        db.execute(query, RequestEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<RequestEntity> update(RequestEntity request) {
        Promise<RequestEntity> promise = Promise.promise();
        String query = QueryBuilder.update(request).build();

        db.executeOne(query, RequestEntity.class,
            _ -> promise.complete(request),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<RequestEntity> delete(Integer id) {
        Promise<RequestEntity> promise = Promise.promise();
        RequestEntity request = new RequestEntity();
        request.setRequest_id(id);

        String query = QueryBuilder.delete(request).build();

        db.executeOne(query, RequestEntity.class,
            _ -> promise.complete(request),
            promise::fail
        );

        return promise.future();
    }
}
