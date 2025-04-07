package net.miarma.api.huertos.dao;

import java.util.List;
import java.util.Map;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.entities.IncomeEntity;
import net.miarma.api.huertos.entities.ViewIncomesWithFullNames;

public class IncomeDAO implements DataAccessObject<IncomeEntity> {

    private final DatabaseManager db;

    public IncomeDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<IncomeEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
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
		Promise<List<ViewIncomesWithFullNames>> promise = Promise.promise();
		String query = QueryBuilder
						.select(ViewIncomesWithFullNames.class)
						.build();
		
		db.execute(query, ViewIncomesWithFullNames.class,
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
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
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
    public Future<IncomeEntity> delete(Integer id) {
        Promise<IncomeEntity> promise = Promise.promise();
        IncomeEntity income = new IncomeEntity();
        income.setIncome_id(id);

        String query = QueryBuilder.delete(income).build();

        db.executeOne(query, IncomeEntity.class,
            _ -> promise.complete(income),
            promise::fail
        );

        return promise.future();
    }
}
