package net.miarma.api.huertos.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.IDataAccessObject;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.huertos.entities.IncomeEntity;

public class IncomeDAO implements IDataAccessObject<IncomeEntity> {

    private final DatabaseManager db;

    public IncomeDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public void getAll(Handler<AsyncResult<List<IncomeEntity>>> handler) {
        String query = QueryBuilder
                .select(IncomeEntity.class)
                .build();

        db.execute(query, IncomeEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void insert(IncomeEntity income, Handler<AsyncResult<IncomeEntity>> handler) {
        String query = QueryBuilder
                .insert(income)
                .build();

        db.execute(query, IncomeEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void update(IncomeEntity income, Handler<AsyncResult<IncomeEntity>> handler) {
        String query = QueryBuilder
                .update(income)
                .build();

        db.execute(query, IncomeEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void delete(Integer id, Handler<AsyncResult<IncomeEntity>> handler) {
        IncomeEntity income = new IncomeEntity();
        income.setIncome_id(id);

        String query = QueryBuilder
                .delete(income)
                .build();

        db.execute(query, IncomeEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }
}