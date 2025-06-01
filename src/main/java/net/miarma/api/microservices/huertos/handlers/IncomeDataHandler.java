package net.miarma.api.microservices.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.IncomeEntity;
import net.miarma.api.microservices.huertos.services.IncomeService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class IncomeDataHandler {
    private final IncomeService incomeService;

    public IncomeDataHandler(Pool pool) {
        this.incomeService = new IncomeService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        incomeService.getAll(params)
            .onSuccess(incomes -> JsonUtil.sendJson(ctx, ApiStatus.OK, incomes))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
    
    public void getIncomesWithNames(RoutingContext ctx) {
		QueryParams params = QueryParams.from(ctx);

		incomeService.getIncomesWithNames(params)
			.onSuccess(incomes -> JsonUtil.sendJson(ctx, ApiStatus.OK, incomes))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}

    public void getById(RoutingContext ctx) {
        Integer incomeId = Integer.parseInt(ctx.pathParam("income_id"));

        incomeService.getById(incomeId)
            .onSuccess(income -> JsonUtil.sendJson(ctx, ApiStatus.OK, income))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        IncomeEntity income = Constants.GSON.fromJson(ctx.body().asString(), IncomeEntity.class);

        incomeService.create(income)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        IncomeEntity income = Constants.GSON.fromJson(ctx.body().asString(), IncomeEntity.class);

        incomeService.update(income)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer incomeId = Integer.parseInt(ctx.pathParam("income_id"));

        incomeService.delete(incomeId)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}