package net.miarma.api.huertos.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.IDataAccessObject;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.huertos.entities.AnnounceEntity;

public class AnnounceDAO implements IDataAccessObject<AnnounceEntity> {

    private final DatabaseManager db;

    public AnnounceDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public void getAll(Handler<AsyncResult<List<AnnounceEntity>>> handler) {
        String query = QueryBuilder
                .select(AnnounceEntity.class)
                .build();

        db.execute(query, AnnounceEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void insert(AnnounceEntity announce, Handler<AsyncResult<AnnounceEntity>> handler) {
        String query = QueryBuilder
                .insert(announce)
                .build();

        db.execute(query, AnnounceEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void update(AnnounceEntity announce, Handler<AsyncResult<AnnounceEntity>> handler) {
        String query = QueryBuilder
                .update(announce)
                .build();

        db.execute(query, AnnounceEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void delete(Integer id, Handler<AsyncResult<AnnounceEntity>> handler) {
        AnnounceEntity announce = new AnnounceEntity();
        announce.setAnnounce_id(id);

        String query = QueryBuilder
                .delete(announce)
                .build();

        db.execute(query, AnnounceEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }
}
