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

import java.util.List;
import java.util.Map;

public class MovieDAO implements DataAccessObject<MovieEntity, Integer> {

    private final DatabaseManager db;

    public MovieDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<MovieEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<MovieEntity> getById(Integer id) {
        Promise<MovieEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(MovieEntity.class)
                .where(Map.of("movie_id", id))
                .build();

        db.executeOne(query, MovieEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<MovieEntity>> getAll(QueryParams params) {
        Promise<List<MovieEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(MovieEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();

        db.execute(query, MovieEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<MovieEntity> insert(MovieEntity movieEntity) {
        Promise<MovieEntity> promise = Promise.promise();
        String query = QueryBuilder
                .insert(movieEntity)
                .build();

        db.executeOne(query, MovieEntity.class,
                _ -> promise.complete(movieEntity),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<MovieEntity> upsert(MovieEntity movieEntity, String... conflictKeys) {
        Promise<MovieEntity> promise = Promise.promise();
        String query = QueryBuilder
                .upsert(movieEntity, conflictKeys)
                .build();

        db.executeOne(query, MovieEntity.class,
                _ -> promise.complete(movieEntity),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<MovieEntity> update(MovieEntity movieEntity) {
        Promise<MovieEntity> promise = Promise.promise();
        String query = QueryBuilder
                .update(movieEntity)
                .build();

        db.executeOne(query, MovieEntity.class,
                _ -> promise.complete(movieEntity),
                promise::fail
        );

        return promise.future();
    }

    public Future<MovieEntity> updateWithNulls(MovieEntity movieEntity) {
        Promise<MovieEntity> promise = Promise.promise();
        String query = QueryBuilder
                .updateWithNulls(movieEntity)
                .build();

        db.executeOne(query, MovieEntity.class,
                _ -> promise.complete(movieEntity),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setMovie_id(id);

        String query = QueryBuilder
                .delete(movieEntity)
                .build();

        db.executeOne(query, MovieEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(MovieEntity.class)
                .where( Map.of("movie_id", id))
                .build();

        db.executeOne(query, MovieEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
