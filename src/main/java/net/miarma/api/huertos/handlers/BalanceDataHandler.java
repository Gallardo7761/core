package net.miarma.api.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.huertos.entities.BalanceEntity;
import net.miarma.api.huertos.services.BalanceService;

@SuppressWarnings("unused")
public class BalanceDataHandler {
	private BalanceService balanceService;
	
	public BalanceDataHandler(Pool pool) {
		this.balanceService = new BalanceService(pool);
	}
	
	public void getBalance(RoutingContext ctx) {
		balanceService.getBalance().onSuccess(balance -> {
			ctx.response().putHeader("Content-Type", "application/json").end(balance.encode());
		}).onFailure(err -> {
			ctx.response().setStatusCode(500).end("{\"error\":\"Internal server error\"}");
		});
	}
	
	public void create(RoutingContext ctx) {
		BalanceEntity balance = Constants.GSON.fromJson(ctx.body().asString(), BalanceEntity.class);
		balanceService.create(balance).onSuccess(result -> {
			ctx.response().setStatusCode(201).end(result.encode());
		}).onFailure(err -> {
			ctx.response().setStatusCode(400).end("{\"error\":\"Bad request\"}");
		});
	}
	
	public void update(RoutingContext ctx) {
		BalanceEntity balance = Constants.GSON.fromJson(ctx.body().asString(), BalanceEntity.class);
		balanceService.update(balance).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end("{\"error\":\"Not found\"}");
		});
	}
}
