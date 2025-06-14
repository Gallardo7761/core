package net.miarma.api.microservices.miarmacraft.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.miarmacraft.entities.UserMetadataEntity;

import java.util.List;
import java.util.Map;

public class UserMetadataDAO implements DataAccessObject<UserMetadataEntity, Integer> {

    private final DatabaseManager db;

    public UserMetadataDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<UserMetadataEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<UserMetadataEntity> getById(Integer id) {
        Promise<UserMetadataEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(UserMetadataEntity.class)
                .where(Map.of("user_id", id.toString()))
                .build();

        db.executeOne(query, UserMetadataEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
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
    public Future<UserMetadataEntity> insert(UserMetadataEntity t) {
        Promise<UserMetadataEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(t).build();
        db.executeOne(query, UserMetadataEntity.class,
                promise::complete,
                promise::fail
        );
        return promise.future();
    }

    @Override
    public Future<UserMetadataEntity> upsert(UserMetadataEntity userMetadataEntity, String... conflictKeys) {
        Promise<UserMetadataEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(userMetadataEntity, conflictKeys).build();
        db.executeOne(query, UserMetadataEntity.class,
                promise::complete,
                promise::fail
        );
        return promise.future();
    }

    @Override
    public Future<UserMetadataEntity> update(UserMetadataEntity t) {
        Promise<UserMetadataEntity> promise = Promise.promise();
        String query = QueryBuilder.update(t).build();
        db.executeOne(query, UserMetadataEntity.class,
                promise::complete,
                promise::fail
        );
        return promise.future();
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        UserMetadataEntity entity = new UserMetadataEntity();
        entity.setUser_id(id);
        String query = QueryBuilder.delete(entity).build();
        db.executeOne(query, UserMetadataEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );
        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer integer) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(UserMetadataEntity.class)
                .where(Map.of("user_id", integer.toString()))
                .build();

        db.executeOne(query, UserMetadataEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

}
