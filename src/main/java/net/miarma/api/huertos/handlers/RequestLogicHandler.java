package net.miarma.api.huertos.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.util.JsonUtil;

public class RequestLogicHandler {
	private final Vertx vertx;
	
	public RequestLogicHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	private void handleError(RoutingContext ctx, Throwable err, String notFoundMsg) {
        ApiStatus status = ApiStatus.fromException(err);
        JsonUtil.sendJson(ctx, status, null, notFoundMsg);
    }
	
	public void getRequestsWithPreUsers(RoutingContext ctx) {
        JsonObject request = new JsonObject().put("action", "getRequestsWithPreUsers");
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "No requests found");
        });
	}
	
	public void getRequestWithPreUser(RoutingContext ctx) {
		Integer requestId = Integer.parseInt(ctx.request().getParam("request_id"));
        JsonObject request = new JsonObject().put("action", "getRequestWithPreUser").put("requestId", requestId);
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "Request not found");
        });
	}
	
	public void getRequestCount(RoutingContext ctx) {
		JsonObject request = new JsonObject().put("action", "getRequestCount");
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "No requests found");
		});
	}
	
	public void getMyRequests(RoutingContext ctx) {
		String token = ctx.request().getHeader("Authorization").substring("Bearer ".length());
		JsonObject request = new JsonObject().put("action", "getMyRequests").put("token", token);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "No requests found");
		});
	}
	
	public void acceptRequest(RoutingContext ctx) {
		Integer requestId = Integer.parseInt(ctx.request().getParam("request_id"));
		JsonObject request = new JsonObject().put("action", "acceptRequest").put("requestId", requestId);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Request not found");
		});
	}
	
	public void rejectRequest(RoutingContext ctx) {
		Integer requestId = Integer.parseInt(ctx.request().getParam("request_id"));
		JsonObject request = new JsonObject().put("action", "rejectRequest").put("requestId", requestId);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Request not found");
		});
	}
	
}
