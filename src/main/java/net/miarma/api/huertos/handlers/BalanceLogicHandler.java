package net.miarma.api.huertos.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.util.JsonUtil;

public class BalanceLogicHandler {
	private Vertx vertx;
	
	public BalanceLogicHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	private void handleError(RoutingContext ctx, Throwable err, String notFoundMsg) {
		ApiStatus status = ApiStatus.fromException(err);
		JsonUtil.sendJson(ctx, status, null, notFoundMsg);
	}
	
	public void getBalanceWithTotals(RoutingContext ctx) {
		JsonObject request = new JsonObject().put("action", "getBalanceWithTotals");
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Balance not found");
		});
	}
}
