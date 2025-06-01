package net.miarma.api.microservices.huertosdecine.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertosdecine.entities.MovieEntity;
import net.miarma.api.microservices.huertosdecine.entities.VoteEntity;

import java.util.List;
import java.util.Map;

public class VoteDAO implements DataAccessObject<VoteEntity> {

    private final DatabaseManager db;

    public VoteDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<VoteEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
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
    public Future<VoteEntity> delete(Integer id) {
        throw new UnsupportedOperationException("Not supported for VoteEntity. Use deleteDoubleId instead.");
    }

    @Override
    public Future<VoteEntity> deleteDoubleId(Integer userId, Integer movieId) {
        Promise<VoteEntity> promise = Promise.promise();
        VoteEntity voteEntity = new VoteEntity();
        voteEntity.setUser_id(userId);
        voteEntity.setMovie_id(movieId);

        return promise.future();
    }
}
