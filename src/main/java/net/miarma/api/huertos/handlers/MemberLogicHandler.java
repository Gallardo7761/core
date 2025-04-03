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
		boolean keepLoggedIn = body.getBoolean("keepLoggedIn", false);
				
		JsonObject request = new JsonObject()
				.put("action", "login")
				.put("emailOrUserName", emailOrUserName)
				.put("password", password)
				.put("keepLoggedIn", keepLoggedIn);
		
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
	
	public void getByMemberNumber(RoutingContext ctx) {
		String memberNumber = ctx.request().getParam("member_number");
		
		JsonObject request = new JsonObject()
				.put("action", "getByMemberNumber")
				.put("memberNumber", memberNumber);
		
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				ctx.response().putHeader("Content-Type", "application/json")
						.end(((JsonObject) ar.result().body()).encode());
			} else {
				ctx.response().setStatusCode(404).end(
					Constants.GSON.toJson(SingleJsonResponse.of("Member not found"))
				);
			}
		});
		
	}
	
	public void getByPlotNumber(RoutingContext ctx) {
		String plotNumber = ctx.request().getParam("plot_number");
		
		JsonObject request = new JsonObject()
				.put("action", "getByPlotNumber")
				.put("plotNumber", plotNumber);
		
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				ctx.response().putHeader("Content-Type", "application/json")
						.end(((JsonObject) ar.result().body()).encode());
			} else {
				ctx.response().setStatusCode(404).end(
					Constants.GSON.toJson(SingleJsonResponse.of("Member not found"))
				);
			}
		});
		
	}
	
	public void getByDNI(RoutingContext ctx) {
		String dni = ctx.request().getParam("dni");
		
		JsonObject request = new JsonObject()
				.put("action", "getByDNI")
				.put("dni", dni);
		
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				ctx.response().putHeader("Content-Type", "application/json")
						.end(((JsonObject) ar.result().body()).encode());
			} else {
				ctx.response().setStatusCode(404).end(
					Constants.GSON.toJson(SingleJsonResponse.of("Member not found"))
				);
			}
		});
		
	}
	
	public void getUserPayments(RoutingContext ctx) {
		String memberNumber = ctx.request().getParam("member_number");
		
		JsonObject request = new JsonObject()
				.put("action", "getUserPayments")
				.put("memberNumber", memberNumber);
		
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				ctx.response().putHeader("Content-Type", "application/json")
						.end(((JsonObject) ar.result().body()).encode());
			} else {
				ctx.response().setStatusCode(404).end(
					Constants.GSON.toJson(SingleJsonResponse.of("Member not found"))
				);
			}
		});
		
	}
	
	public void hasPaid(RoutingContext ctx) {
		String memberNumber = ctx.request().getParam("member_number");
		
		JsonObject request = new JsonObject()
				.put("action", "hasPaid")
				.put("memberNumber", memberNumber);
		
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				ctx.response().putHeader("Content-Type", "application/json")
						.end(((JsonObject) ar.result().body()).encode());
			} else {
				ctx.response().setStatusCode(404).end(
					Constants.GSON.toJson(SingleJsonResponse.of("Member not found"))
				);
			}
		});
		
	}
}
