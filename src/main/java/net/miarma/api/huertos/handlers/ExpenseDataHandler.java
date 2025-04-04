package net.miarma.api.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.entities.ExpenseEntity;
import net.miarma.api.huertos.services.ExpenseService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class ExpenseDataHandler {
    private final ExpenseService expenseService;

    public ExpenseDataHandler(Pool pool) {
        this.expenseService = new ExpenseService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        expenseService.getAll(params)
            .onSuccess(expenses -> JsonUtil.sendJson(ctx, ApiStatus.OK, expenses))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer expenseId = Integer.parseInt(ctx.pathParam("expense_id"));

        expenseService.getById(expenseId)
            .onSuccess(expense -> JsonUtil.sendJson(ctx, ApiStatus.OK, expense))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        ExpenseEntity expense = Constants.GSON.fromJson(ctx.body().asString(), ExpenseEntity.class);

        expenseService.create(expense)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        ExpenseEntity expense = Constants.GSON.fromJson(ctx.body().asString(), ExpenseEntity.class);

        expenseService.update(expense)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer expenseId = Integer.parseInt(ctx.pathParam("expense_id"));

        expenseService.delete(expenseId)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}