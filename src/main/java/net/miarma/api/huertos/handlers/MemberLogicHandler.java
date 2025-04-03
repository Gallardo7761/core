package net.miarma.api.huertos.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.SingleJsonResponse;

public class MemberLogicHandler {
	private Vertx vertx;
	
	public MemberLogicHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void login(RoutingContext ctx) {
		JsonObject body = ctx.body().asJsonObject();
		String emailOrUserName = body.getString("email") != null ? 
				body.getString("email") : body.getString("userName");
		String password = body.getString("password");
		
		JsonObject request = new JsonObject()
				.put("action", "login")
				.put("emailOrUserName", emailOrUserName)
				.put("password", password);
		
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body())
                        		.put("tokenTime", System.currentTimeMillis())
                        		.encode());
            } else {
                ctx.response().setStatusCode(401).end(
            		Constants.GSON.toJson(SingleJsonResponse.of("The member is inactive or banned"))
        		);
            }
		});
	}
}
