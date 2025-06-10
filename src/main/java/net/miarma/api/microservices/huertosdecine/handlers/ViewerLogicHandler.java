package net.miarma.api.microservices.huertosdecine.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.JsonUtil;

public class ViewerLogicHandler {
    private final Vertx vertx;

    public ViewerLogicHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void getVotesOnMovieByUserId(RoutingContext ctx) {
        Integer movieId = Integer.parseInt(ctx.request().getParam("movie_id"));
        Integer userId = Integer.parseInt(ctx.request().getParam("viewer_id"));
        JsonObject request = new JsonObject().put("action", "getVotesOnMovieByUserId").put("user_id", userId).put("movie_id", movieId);
        vertx.eventBus().request(Constants.CINE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else EventBusUtil.handleReplyError(ctx, ar.cause(), "No votes found for this movie and viewer");
        });
    }

    public void login(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        JsonObject request = new JsonObject()
            .put("action", "login")
            .put("email", body.getString("email", null))
            .put("userName", body.getString("userName", null))
            .put("password", body.getString("password"))
            .put("keepLoggedIn", body.getBoolean("keepLoggedIn", false));

        vertx.eventBus().request(Constants.CINE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonObject result = (JsonObject) ar.result().body();
                result.put("tokenTime", System.currentTimeMillis());
                JsonUtil.sendJson(ctx, ApiStatus.OK, result);
            } else {
                EventBusUtil.handleReplyError(ctx, ar.cause(), "The viewer is inactive or banned");
            }
        });
    }
}
