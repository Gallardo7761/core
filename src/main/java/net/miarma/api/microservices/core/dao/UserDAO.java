package net.miarma.api.microservices.core.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.core.entities.UserEntity;

import java.util.List;
import java.util.Map;

public class UserDAO implements DataAccessObject<UserEntity, Integer> {

    private final DatabaseManager db;

    public UserDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<UserEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<UserEntity> getById(Integer id) {
        Promise<UserEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(UserEntity.class)
                .where(Map.of("user_id", id.toString()))
                .build();

        db.executeOne(query, UserEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<UserEntity>> getAll(QueryParams params) {
        Promise<List<UserEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(UserEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();

        db.execute(query, UserEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    public Future<UserEntity> getByEmail(String email) {
        Promise<UserEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(UserEntity.class)
                .where(Map.of("email", email))
                .build();

        db.executeOne(query, UserEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<UserEntity> getByUserName(String userName) {
        Promise<UserEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(UserEntity.class)
                .where(Map.of("user_name", userName))
                .build();

        db.executeOne(query, UserEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<UserEntity> insert(UserEntity user) {
        Promise<UserEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(user).build();

        db.executeOne(query, UserEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<UserEntity> upsert(UserEntity userEntity, String... conflictKeys) {
        Promise<UserEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(userEntity, conflictKeys).build();

        db.executeOne(query, UserEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<UserEntity> update(UserEntity user) {
        Promise<UserEntity> promise = Promise.promise();
        String query = QueryBuilder.update(user).build();

        db.executeOne(query, UserEntity.class,
                _ -> promise.complete(user),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        UserEntity user = new UserEntity();
        user.setUser_id(id);

        String query = QueryBuilder.delete(user).build();

        db.executeOne(query, UserEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(UserEntity.class)
                .where(Map.of("user_id", id.toString()))
                .build();

        db.executeOne(query, UserEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
