package net.miarma.api.huertos.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.huertos.entities.PreUserEntity;

public class PreUserDAO implements DataAccessObject<PreUserEntity> {

    private final DatabaseManager db;

    public PreUserDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public void getAll(Handler<AsyncResult<List<PreUserEntity>>> handler) {
        String query = QueryBuilder
                .select(PreUserEntity.class)
                .build();

        db.execute(query, PreUserEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void insert(PreUserEntity preUser, Handler<AsyncResult<PreUserEntity>> handler) {
        String query = QueryBuilder
                .insert(preUser)
                .build();

        db.execute(query, PreUserEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void update(PreUserEntity preUser, Handler<AsyncResult<PreUserEntity>> handler) {
        String query = QueryBuilder
                .update(preUser)
                .build();

        db.execute(query, PreUserEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void delete(Integer id, Handler<AsyncResult<PreUserEntity>> handler) {
        PreUserEntity preUser = new PreUserEntity();
        preUser.setPre_user_id(id);

        String query = QueryBuilder
                .delete(preUser)
                .build();

        db.execute(query, PreUserEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }
}
