package net.miarma.api.microservices.miarmacraft.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.miarmacraft.entities.ModEntity;

import java.util.List;
import java.util.Map;

public class ModDAO implements DataAccessObject<ModEntity, Integer> {

    private final DatabaseManager db;

    public ModDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<ModEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<ModEntity> getById(Integer integer) {
        Promise<ModEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(ModEntity.class)
                .where(Map.of("mod_id", integer.toString()))
                .build();

        db.executeOne(query, ModEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<ModEntity>> getAll(QueryParams params) {
        Promise<List<ModEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(ModEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();
        db.execute(query, ModEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );
        return promise.future();
    }

    @Override
    public Future<ModEntity> insert(ModEntity mod) {
        Promise<ModEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(mod).build();

        db.executeOne(query, ModEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<ModEntity> upsert(ModEntity modEntity, String... conflictKeys) {
        Promise<ModEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(modEntity, conflictKeys).build();

        db.executeOne(query, ModEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<ModEntity> update(ModEntity mod) {
        Promise<ModEntity> promise = Promise.promise();
        String query = QueryBuilder.update(mod).build();

        db.executeOne(query, ModEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        ModEntity mod = new ModEntity();
        mod.setMod_id(id);
        String query = QueryBuilder
                .delete(mod)
                .build();
        db.executeOne(query, ModEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );
        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(ModEntity.class)
                .where(Map.of("mod_id", id.toString()))
                .build();

        db.executeOne(query, ModEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

}
