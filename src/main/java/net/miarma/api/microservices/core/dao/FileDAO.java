package net.miarma.api.microservices.core.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.core.entities.FileEntity;

import java.util.List;
import java.util.Map;

public class FileDAO implements DataAccessObject<FileEntity, Integer> {

    private final DatabaseManager db;

    public FileDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<FileEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<FileEntity> getById(Integer id) {
        Promise<FileEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(FileEntity.class)
                .where(Map.of("file_id", id.toString()))
                .build();

        db.executeOne(query, FileEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<FileEntity>> getAll(QueryParams params) {
        Promise<List<FileEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(FileEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();

        db.execute(query, FileEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    public Future<List<FileEntity>> getUserFiles(Integer userId) {
        Promise<List<FileEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(FileEntity.class)
                .where(Map.of("uploaded_by", userId.toString()))
                .build();

        db.execute(query, FileEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<FileEntity> insert(FileEntity file) {
        Promise<FileEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(file).build();

        db.executeOne(query, FileEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<FileEntity> upsert(FileEntity file, String... conflictKeys) {
        Promise<FileEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(file, conflictKeys).build();

        db.executeOne(query, FileEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<FileEntity> update(FileEntity file) {
        Promise<FileEntity> promise = Promise.promise();
        String query = QueryBuilder.update(file).build();

        db.executeOne(query, FileEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(FileEntity.class)
                .where(Map.of("file_id", id.toString()))
                .build();

        db.executeOne(query, FileEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        FileEntity file = new FileEntity();
        file.setFile_id(id);

        String query = QueryBuilder.delete(file).build();

        db.executeOne(query, FileEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
