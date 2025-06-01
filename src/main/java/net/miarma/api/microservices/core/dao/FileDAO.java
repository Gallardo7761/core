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

public class FileDAO implements DataAccessObject<FileEntity> {

    private final DatabaseManager db;

    public FileDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<FileEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
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

    @Override
    public Future<FileEntity> insert(FileEntity file) {
        Promise<FileEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(file).build();

        db.executeOne(query, FileEntity.class,
    		result -> promise.complete(result),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<FileEntity> update(FileEntity file) {
        Promise<FileEntity> promise = Promise.promise();
        String query = QueryBuilder.update(file).build();

        db.executeOne(query, FileEntity.class,
    		result -> promise.complete(result),
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

        db.executeOne(query, FileEntity.class,
    		result -> promise.complete(result),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<FileEntity> deleteDoubleId(Integer id1, Integer id2) {
        throw new UnsupportedOperationException("Method not implemented for FileDAO");
    }
}
