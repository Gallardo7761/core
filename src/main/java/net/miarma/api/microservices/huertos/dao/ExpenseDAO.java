package net.miarma.api.microservices.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.ExpenseEntity;

import java.util.List;
import java.util.Map;

public class ExpenseDAO implements DataAccessObject<ExpenseEntity, Integer> {

    private final DatabaseManager db;

    public ExpenseDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<ExpenseEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<ExpenseEntity> getById(Integer id) {
        Promise<ExpenseEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(ExpenseEntity.class)
                .where(Map.of("expense_id", id))
                .build();

        db.executeOne(query, ExpenseEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
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
                list -> promise.complete(list.isEmpty() ? null : list.getFirst()),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<ExpenseEntity> upsert(ExpenseEntity expenseEntity, String... conflictKeys) {
        Promise<ExpenseEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(expenseEntity, conflictKeys).build();

        db.executeOne(query, ExpenseEntity.class,
                promise::complete,
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
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        ExpenseEntity expense = new ExpenseEntity();
        expense.setExpense_id(id);

        String query = QueryBuilder.delete(expense).build();

        db.executeOne(query, ExpenseEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(ExpenseEntity.class)
                .where(Map.of("expense_id", id))
                .build();

        db.executeOne(query, ExpenseEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
