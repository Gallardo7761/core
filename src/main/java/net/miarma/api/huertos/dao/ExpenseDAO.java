package net.miarma.api.huertos.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.IDataAccessObject;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.huertos.entities.ExpenseEntity;

public class ExpenseDAO implements IDataAccessObject<ExpenseEntity> {

    private final DatabaseManager db;

    public ExpenseDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public void getAll(Handler<AsyncResult<List<ExpenseEntity>>> handler) {
        String query = QueryBuilder
                .select(ExpenseEntity.class)
                .build();

        db.execute(query, ExpenseEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void insert(ExpenseEntity expense, Handler<AsyncResult<ExpenseEntity>> handler) {
        String query = QueryBuilder
                .insert(expense)
                .build();

        db.execute(query, ExpenseEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void update(ExpenseEntity expense, Handler<AsyncResult<ExpenseEntity>> handler) {
        String query = QueryBuilder
                .update(expense)
                .build();

        db.execute(query, ExpenseEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void delete(Integer id, Handler<AsyncResult<ExpenseEntity>> handler) {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setExpense_id(id);

        String query = QueryBuilder
                .delete(expense)
                .build();

        db.execute(query, ExpenseEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }
}
