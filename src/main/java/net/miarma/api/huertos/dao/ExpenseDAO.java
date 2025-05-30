package net.miarma.api.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.entities.ExpenseEntity;

import java.util.List;
import java.util.Map;

public class ExpenseDAO implements DataAccessObject<ExpenseEntity> {

    private final DatabaseManager db;

    public ExpenseDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<ExpenseEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }
    
    public Future<List<ExpenseEntity>> getAll(QueryParams params) {
        Promise<List<ExpenseEntity>> promise = Promise.promise();
        String query = QueryBuilder
        		.select(ExpenseEntity.class)
        		.where(params.getFilters())
				.orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
				.limit(params.getQueryFilters().getLimit())
				.offset(params.getQueryFilters().getOffset())
				.build();

        db.execute(query, ExpenseEntity.class,
            list -> promise.complete(list.isEmpty() ? List.of() : list),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<ExpenseEntity> insert(ExpenseEntity expense) {
        Promise<ExpenseEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(expense).build();

        db.execute(query, ExpenseEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<ExpenseEntity> update(ExpenseEntity expense) {
        Promise<ExpenseEntity> promise = Promise.promise();
        String query = QueryBuilder.update(expense).build();

        db.executeOne(query, ExpenseEntity.class,
            _ -> promise.complete(expense),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<ExpenseEntity> delete(Integer id) {
        Promise<ExpenseEntity> promise = Promise.promise();
        ExpenseEntity expense = new ExpenseEntity();
        expense.setExpense_id(id);

        String query = QueryBuilder.delete(expense).build();

        db.executeOne(query, ExpenseEntity.class,
            _ -> promise.complete(expense),
            promise::fail
        );

        return promise.future();
    }
}
