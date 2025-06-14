package net.miarma.api.microservices.huertosdecine.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertosdecine.entities.VoteEntity;

import java.util.List;
import java.util.Map;

public class VoteDAO implements DataAccessObject<VoteEntity, Integer> {

    private final DatabaseManager db;

    public VoteDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<VoteEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<VoteEntity> getById(Integer integer) {
        Promise<VoteEntity> promise = Promise.promise();
        String query = QueryBuilder
            .select(VoteEntity.class)
            .where(Map.of("movie_id", integer.toString()))
            .build();

        db.executeOne(query, VoteEntity.class,
                promise::complete,
            promise::fail
        );

        return promise.future();
    }

    public Future<List<VoteEntity>> getAll(QueryParams params) {
        Promise<List<VoteEntity>> promise = Promise.promise();
        String query = QueryBuilder
            .select(VoteEntity.class)
            .where(params.getFilters())
            .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
            .limit(params.getQueryFilters().getLimit())
            .offset(params.getQueryFilters().getOffset())
            .build();

        db.execute(query, VoteEntity.class,
            list -> promise.complete(list.isEmpty() ? List.of() : list),
            promise::fail
        );

        return promise.future();
    }

    public Future<List<VoteEntity>> getVotesByMovieId(Integer movieId) {
        Promise<List<VoteEntity>> promise = Promise.promise();
        String query = QueryBuilder
            .select(VoteEntity.class)
            .where(Map.of("movie_id", movieId.toString()))
            .build();

        db.execute(query, VoteEntity.class,
            list -> promise.complete(list.isEmpty() ? List.of() : list),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<VoteEntity> insert(VoteEntity voteEntity) {
        Promise<VoteEntity> promise = Promise.promise();
        String query = QueryBuilder
            .insert(voteEntity)
            .build();

        db.executeOne(query, VoteEntity.class,
            _ -> promise.complete(voteEntity),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<VoteEntity> upsert(VoteEntity voteEntity, String... conflictKeys) {
        Promise<VoteEntity> promise = Promise.promise();
        String query = QueryBuilder
            .upsert(voteEntity, conflictKeys)
            .build();

        db.executeOne(query, VoteEntity.class,
            _ -> promise.complete(voteEntity),
            promise::fail
        );

        return promise.future();
    }

    public Future<VoteEntity> upsert(VoteEntity voteEntity) {
        Promise<VoteEntity> promise = Promise.promise();
        String query = QueryBuilder
            .upsert(voteEntity, "user_id", "movie_id")
            .build();

        db.executeOne(query, VoteEntity.class,
            _ -> promise.complete(voteEntity),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<VoteEntity> update(VoteEntity voteEntity) {
        Promise<VoteEntity> promise = Promise.promise();
        String query = QueryBuilder
            .update(voteEntity)
            .build();

        db.executeOne(query, VoteEntity.class,
            _ -> promise.complete(voteEntity),
            promise::fail
        );

        return promise.future();
    }

    public Future<VoteEntity> updateWithNulls(VoteEntity voteEntity) {
        Promise<VoteEntity> promise = Promise.promise();
        String query = QueryBuilder
            .updateWithNulls(voteEntity)
            .build();

        db.executeOne(query, VoteEntity.class,
            _ -> promise.complete(voteEntity),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        VoteEntity voteEntity = new VoteEntity();
        voteEntity.setMovie_id(id);

        String query = QueryBuilder
            .delete(voteEntity)
            .build();

        db.executeOne(query, VoteEntity.class,
            result -> promise.complete(result != null),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer integer) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
            .select(VoteEntity.class)
            .where(Map.of("movie_id", integer.toString()))
            .build();

        db.executeOne(query, VoteEntity.class,
            result -> promise.complete(result != null),
            promise::fail
        );

        return promise.future();
    }

}
