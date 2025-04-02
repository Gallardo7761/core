package net.miarma.api.core.dao;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.core.entities.FileEntity;

public class FileDAO implements DataAccessObject<FileEntity> {

    private final DatabaseManager db;

    public FileDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<FileEntity>> getAll() {
        Promise<List<FileEntity>> promise = Promise.promise();
        String query = QueryBuilder.select(FileEntity.class).build();

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

        db.execute(query, FileEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<FileEntity> update(FileEntity file) {
        Promise<FileEntity> promise = Promise.promise();
        String query = QueryBuilder.update(file).build();

        db.execute(query, FileEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<FileEntity> delete(Integer id) {
        Promise<FileEntity> promise = Promise.promise();
        FileEntity file = new FileEntity();
        file.setFile_id(id);

        String query = QueryBuilder.delete(file).build();

        db.execute(query, FileEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }
}
