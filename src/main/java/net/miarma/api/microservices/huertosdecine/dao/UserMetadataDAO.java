package net.miarma.api.microservices.huertosdecine.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertosdecine.entities.UserMetadataEntity;

import java.util.List;
import java.util.Map;

public class UserMetadataDAO implements DataAccessObject<UserMetadataEntity> {

    private final DatabaseManager db;

    public UserMetadataDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<UserMetadataEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
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
    public Future<UserMetadataEntity> insert(UserMetadataEntity metadata) {
        Promise<UserMetadataEntity> promise = Promise.promise();
        String query = QueryBuilder
            .insert(metadata)
            .build();
        
        db.executeOne(query, UserMetadataEntity.class,
            _ -> promise.complete(),
            promise::fail
        );
        
        return promise.future();
    }

    @Override
    public Future<UserMetadataEntity> update(UserMetadataEntity metadata) {
        Promise<UserMetadataEntity> promise = Promise.promise();
        String query = QueryBuilder
            .update(metadata)
            .build();

        db.executeOne(query, UserMetadataEntity.class,
            _ -> promise.complete(metadata),
            promise::fail
        );

        return promise.future();
    }

    public Future<UserMetadataEntity> updateWithNulls(UserMetadataEntity metadata) {
        Promise<UserMetadataEntity> promise = Promise.promise();
        String query = QueryBuilder.updateWithNulls(metadata).build();

        db.executeOne(query, UserMetadataEntity.class,
                _ -> promise.complete(metadata),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<UserMetadataEntity> delete(Integer id) {
        Promise<UserMetadataEntity> promise = Promise.promise();
        UserMetadataEntity metadata = new UserMetadataEntity();
        metadata.setUser_id(id);

        String query = QueryBuilder.delete(metadata).build();

        db.executeOne(query, UserMetadataEntity.class,
            _ -> promise.complete(metadata),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<UserMetadataEntity> deleteDoubleId(Integer id1, Integer id2) {
        throw new UnsupportedOperationException("Not supported for UserMetadataEntity. Use delete instead.");
    }
}
