package net.miarma.api.microservices.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.IncomeEntity;
import net.miarma.api.microservices.huertos.entities.ViewIncomesWithFullNames;

import java.util.List;
import java.util.Map;

public class IncomeDAO implements DataAccessObject<IncomeEntity, Integer> {

    private final DatabaseManager db;

    public IncomeDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<IncomeEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<IncomeEntity> getById(Integer id) {
        Promise<IncomeEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(IncomeEntity.class)
                .where(Map.of("income_id", id.toString()))
                .build();

        db.executeOne(query, IncomeEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<IncomeEntity>> getAll(QueryParams params) {
        Promise<List<IncomeEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(IncomeEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();

        db.execute(query, IncomeEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    public Future<List<ViewIncomesWithFullNames>> getAllWithNames() {
        return getAllWithNames(new QueryParams(Map.of(), new QueryFilters()));
    }

    public Future<List<ViewIncomesWithFullNames>> getAllWithNames(QueryParams params) {
        Promise<List<ViewIncomesWithFullNames>> promise = Promise.promise();
        String query = QueryBuilder
                .select(ViewIncomesWithFullNames.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();

        db.execute(query, ViewIncomesWithFullNames.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    public Future<List<IncomeEntity>> getUserIncomes(Integer memberNumber) {
        Promise<List<IncomeEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(IncomeEntity.class)
                .where(Map.of("member_number", memberNumber.toString()))
                .build();

        db.execute(query, IncomeEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<IncomeEntity> insert(IncomeEntity income) {
        Promise<IncomeEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(income).build();

        db.execute(query, IncomeEntity.class,
                list -> promise.complete(list.isEmpty() ? null : list.getFirst()),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<IncomeEntity> upsert(IncomeEntity incomeEntity, String... conflictKeys) {
        Promise<IncomeEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(incomeEntity, conflictKeys).build();

        db.executeOne(query, IncomeEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<IncomeEntity> update(IncomeEntity income) {
        Promise<IncomeEntity> promise = Promise.promise();
        String query = QueryBuilder.update(income).build();

        db.executeOne(query, IncomeEntity.class,
                _ -> promise.complete(income),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        IncomeEntity income = new IncomeEntity();
        income.setIncome_id(id);

        String query = QueryBuilder.delete(income).build();

        db.executeOne(query, IncomeEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(IncomeEntity.class)
                .where(Map.of("income_id", id.toString()))
                .build();

        db.executeOne(query, IncomeEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
