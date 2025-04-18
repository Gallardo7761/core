package net.miarma.api.huertos.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.util.JsonUtil;

public class MemberLogicHandler {
    private final Vertx vertx;

    public MemberLogicHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    private void handleError(RoutingContext ctx, Throwable err, String notFoundMsg) {
        ApiStatus status = ApiStatus.fromException(err);
        JsonUtil.sendJson(ctx, status, null, notFoundMsg);
    }

    public void login(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        JsonObject request = new JsonObject()
            .put("action", "login")
            .put("email", body.getString("email", null))
            .put("userName", body.getString("userName", null))
            .put("password", body.getString("password"))
            .put("keepLoggedIn", body.getBoolean("keepLoggedIn", false));

        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonObject result = (JsonObject) ar.result().body();
                result.put("tokenTime", System.currentTimeMillis());
                JsonUtil.sendJson(ctx, ApiStatus.OK, result);
            } else {
                handleError(ctx, ar.cause(), "The member is inactive or banned");
            }
        });
    }

    public void getByMemberNumber(RoutingContext ctx) {
        Integer memberNumber = Integer.parseInt(ctx.request().getParam("member_number"));
        JsonObject request = new JsonObject().put("action", "getByMemberNumber").put("memberNumber", memberNumber);
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "Member not found");
        });
    }

    public void getByPlotNumber(RoutingContext ctx) {
        Integer plotNumber = Integer.parseInt(ctx.request().getParam("plot_number"));
        JsonObject request = new JsonObject().put("action", "getByPlotNumber").put("plotNumber", plotNumber);
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "Member not found");
        });
    }

    public void getByDni(RoutingContext ctx) {
        String dni = ctx.request().getParam("dni");
        JsonObject request = new JsonObject().put("action", "getByDNI").put("dni", dni);
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "Member not found");
        });
    }

    public void getUserPayments(RoutingContext ctx) {
        Integer memberNumber = Integer.parseInt(ctx.request().getParam("member_number"));
        JsonObject request = new JsonObject().put("action", "getUserPayments").put("memberNumber", memberNumber);
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "Member not found");
        });
    }

    public void hasPaid(RoutingContext ctx) {
        Integer memberNumber = Integer.parseInt(ctx.request().getParam("member_number"));
        JsonObject request = new JsonObject().put("action", "hasPaid").put("memberNumber", memberNumber);
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "Member not found");
        });
    }

    public void getWaitlist(RoutingContext ctx) {
        JsonObject request = new JsonObject().put("action", "getWaitlist");
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "Waitlist not found");
        });
    }
    
    public void getLimitedWaitlist(RoutingContext ctx) {
		JsonObject request = new JsonObject().put("action", "getLimitedWaitlist");
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Waitlist not found");
		});
	}

    public void getLastMemberNumber(RoutingContext ctx) {
        JsonObject request = new JsonObject().put("action", "getLastMemberNumber");
        vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else handleError(ctx, ar.cause(), "Last member number not found");
        });
    }
    
    public void getProfile(RoutingContext ctx) {
    	String token = ctx.request().getHeader("Authorization").substring("Bearer ".length());
		JsonObject request = new JsonObject().put("action", "getProfile").put("token", token);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Profile not found");
		});
	}
    
    public void hasCollaborator(RoutingContext ctx) {
		String token = ctx.request().getHeader("Authorization").substring("Bearer ".length());	
		JsonObject request = new JsonObject().put("action", "hasCollaborator").put("token", token);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Profile not found");
		});
	}
    
    public void hasCollaboratorRequest(RoutingContext ctx) {
		String token = ctx.request().getHeader("Authorization").substring("Bearer ".length());
		JsonObject request = new JsonObject().put("action", "hasCollaboratorRequest").put("token", token);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Profile not found");
		});
	}
    
    public void hasGreenHouse(RoutingContext ctx) {
		String token = ctx.request().getHeader("Authorization").substring("Bearer ".length());
    	JsonObject request = new JsonObject().put("action", "hasGreenHouse").put("token", token);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Profile not found");
		});
    }
    
    public void hasGreenHouseRequest(RoutingContext ctx) {
		String token = ctx.request().getHeader("Authorization").substring("Bearer ".length());
		JsonObject request = new JsonObject().put("action", "hasGreenHouseRequest").put("token", token);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Profile not found");
		});
    }
    
    public void changeMemberStatus(RoutingContext ctx) {
		Integer memberNumber = Integer.parseInt(ctx.request().getParam("member_number"));
		String status = ctx.request().getParam("status");
		JsonObject request = new JsonObject()
			.put("action", "changeMemberStatus")
			.put("memberNumber", memberNumber)
			.put("status", status);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Member not found");
		});
	}
    
    public void changeMemberType(RoutingContext ctx) {
		Integer memberNumber = Integer.parseInt(ctx.request().getParam("member_number"));
		String type = ctx.request().getParam("type");
		JsonObject request = new JsonObject()
			.put("action", "changeMemberType")
			.put("memberNumber", memberNumber)
			.put("type", type);
		vertx.eventBus().request(Constants.HUERTOS_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else handleError(ctx, ar.cause(), "Member not found");
		});
    }
    
}