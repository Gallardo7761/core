package net.miarma.api.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.huertos.entities.BalanceEntity;
import net.miarma.api.huertos.services.BalanceService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class BalanceDataHandler {
    private final BalanceService balanceService;

    public BalanceDataHandler(Pool pool) {
        this.balanceService = new BalanceService(pool);
    }

    public void getBalance(RoutingContext ctx) {
        balanceService.getBalance()
            .onSuccess(balance -> JsonUtil.sendJson(ctx, ApiStatus.OK, balance))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        BalanceEntity balance = Constants.GSON.fromJson(ctx.body().asString(), BalanceEntity.class);

        balanceService.create(balance)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        BalanceEntity balance = Constants.GSON.fromJson(ctx.body().asString(), BalanceEntity.class);

        balanceService.update(balance)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}