package net.miarma.api.microservices.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.RequestEntity;
import net.miarma.api.microservices.huertos.entities.ViewRequestsWithPreUsers;

import java.util.List;
import java.util.Map;

public class RequestDAO implements DataAccessObject<RequestEntity, Integer> {

    private final DatabaseManager db;

    public RequestDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<RequestEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<RequestEntity> getById(Integer id) {
        Promise<RequestEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(RequestEntity.class)
                .where(Map.of("request_id", id))
                .build();

        db.executeOne(query, RequestEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
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

    public Future<List<ViewRequestsWithPreUsers>> getRequestsWithPreUsers() {
        Promise<List<ViewRequestsWithPreUsers>> promise = Promise.promise();
        String query = QueryBuilder
                .select(ViewRequestsWithPreUsers.class)
                .build();

        db.execute(query, ViewRequestsWithPreUsers.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    public Future<ViewRequestsWithPreUsers> getRequestWithPreUserById(Integer id) {
        Promise<ViewRequestsWithPreUsers> promise = Promise.promise();
        String query = QueryBuilder
                .select(ViewRequestsWithPreUsers.class)
                .where(Map.of("request_id", id))
                .build();
        db.executeOne(query, ViewRequestsWithPreUsers.class,
                promise::complete,
                promise::fail
        );
        return promise.future();
    }

    public Future<List<RequestEntity>> getByUserId(Integer userId) {
        Promise<List<RequestEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(RequestEntity.class)
                .where(Map.of("requested_by", userId))
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
                list -> promise.complete(list.isEmpty() ? null : list.getFirst()),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<RequestEntity> upsert(RequestEntity requestEntity, String... conflictKeys) {
        Promise<RequestEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(requestEntity, conflictKeys).build();

        db.executeOne(query, RequestEntity.class,
                promise::complete,
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
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        RequestEntity request = new RequestEntity();
        request.setRequest_id(id);

        String query = QueryBuilder.delete(request).build();

        db.executeOne(query, RequestEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(RequestEntity.class)
                .where(Map.of("request_id", id))
                .build();

        db.executeOne(query, RequestEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
