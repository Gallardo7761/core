package net.miarma.api.microservices.mpaste.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.mpaste.entities.PasteEntity;

import java.util.List;
import java.util.Map;

public class PasteDAO implements DataAccessObject<PasteEntity, Long> {

    private final DatabaseManager db;

    public PasteDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<PasteEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    public Future<List<PasteEntity>> getAll(QueryParams params) {
        Promise<List<PasteEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(PasteEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();

        db.execute(query, PasteEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );
        return promise.future();
    }

    @Override
    public Future<PasteEntity> getById(Long id) {
    	throw new UnsupportedOperationException("You cannot get this type by its ID");
    }
    
    public Future<PasteEntity> getByKey(String key) {
    	Promise<PasteEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(PasteEntity.class)
                .where(Map.of("paste_key", key))
                .build();

        db.executeOne(query, PasteEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<PasteEntity> insert(PasteEntity paste) {
        Promise<PasteEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(paste).build();

        db.executeOne(query, PasteEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<PasteEntity> upsert(PasteEntity paste, String... conflictKeys) {
    	throw new UnsupportedOperationException("Upsert not supported on this type");
    }

    @Override
    public Future<PasteEntity> update(PasteEntity paste) {
    	throw new UnsupportedOperationException("Update not supported on this type");
    }

    @Override
    public Future<Boolean> delete(Long id) {
        Promise<Boolean> promise = Promise.promise();
        PasteEntity paste = new PasteEntity();
        paste.setPaste_id(id);
        String query = QueryBuilder
                .delete(paste)
                .build();

        db.executeOne(query, PasteEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );
        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Long id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(PasteEntity.class)
                .where(Map.of("paste_id", id.toString()))
                .build();

        db.executeOne(query, PasteEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
