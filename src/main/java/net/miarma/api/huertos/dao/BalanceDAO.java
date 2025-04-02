package net.miarma.api.huertos.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.huertos.entities.BalanceEntity;

public class BalanceDAO implements DataAccessObject<BalanceEntity> {

    private final DatabaseManager db;

    public BalanceDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public void getAll(Handler<AsyncResult<List<BalanceEntity>>> handler) {
        String query = QueryBuilder
                .select(BalanceEntity.class)
                .build();

        db.execute(query, BalanceEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }
    
    @Override
    public void insert(BalanceEntity balance, Handler<AsyncResult<BalanceEntity>> handler) {
        String query = QueryBuilder
                .insert(balance)
                .build();

        db.execute(query, BalanceEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void update(BalanceEntity balance, Handler<AsyncResult<BalanceEntity>> handler) {
        String query = QueryBuilder
                .update(balance)
                .build();

        db.execute(query, BalanceEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void delete(Integer id, Handler<AsyncResult<BalanceEntity>> handler) {
        BalanceEntity balance = new BalanceEntity();
        balance.setId(id);

        String query = QueryBuilder
                .delete(balance)
                .build();

        db.execute(query, BalanceEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }
}
