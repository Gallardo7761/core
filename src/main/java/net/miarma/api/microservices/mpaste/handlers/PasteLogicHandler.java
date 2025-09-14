package net.miarma.api.microservices.mpaste.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.JsonUtil;

public class PasteLogicHandler {
	private final Vertx vertx;
	
	public PasteLogicHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void getByKey(RoutingContext ctx) {
		String key = ctx.request().getParam("paste_key");
		String password = ctx.request().getHeader("X-Paste-Password");
		JsonObject request = new JsonObject()
			.put("action", "getByKey")
			.put("key", key)
			.put("password", password);
		
		vertx.eventBus().request(Constants.MPASTE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else EventBusUtil.handleReplyError(ctx, ar.cause(), "Paste not found");
        });
	}
}
