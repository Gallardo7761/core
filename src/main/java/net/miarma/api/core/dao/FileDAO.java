package net.miarma.api.core.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
    public void getAll(Handler<AsyncResult<List<FileEntity>>> handler) {
        String query = QueryBuilder
                .select(FileEntity.class)
                .build();

        db.execute(query, FileEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void insert(FileEntity file, Handler<AsyncResult<FileEntity>> handler) {
        String query = QueryBuilder
                .insert(file)
                .build();

        db.execute(query, FileEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void update(FileEntity file, Handler<AsyncResult<FileEntity>> handler) {
        String query = QueryBuilder
                .update(file)
                .build();

        db.execute(query, FileEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void delete(Integer id, Handler<AsyncResult<FileEntity>> handler) {
        FileEntity file = new FileEntity();
        file.setFile_id(id);

        String query = QueryBuilder
                .delete(file)
                .build();

        db.execute(query, FileEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }
}
