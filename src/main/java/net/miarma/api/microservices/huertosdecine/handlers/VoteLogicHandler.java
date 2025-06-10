package net.miarma.api.microservices.huertosdecine.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.microservices.huertosdecine.entities.VoteEntity;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.JsonUtil;

public class VoteLogicHandler {
    private final Vertx vertx;

    public VoteLogicHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void getVotes(RoutingContext ctx) {
        Integer movieId = Integer.parseInt(ctx.request().getParam("movie_id"));
        JsonObject request = new JsonObject().put("action", "getVotes").put("movie_id", movieId);
        vertx.eventBus().request(Constants.CINE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else EventBusUtil.handleReplyError(ctx, ar.cause(), "No votes found for this movie and viewer");
        });
    }

    public void addVote(RoutingContext ctx) {
        Integer movieId = Integer.parseInt(ctx.request().getParam("movie_id"));
        VoteEntity vote = Constants.GSON.fromJson(ctx.body().asString(), VoteEntity.class);
        JsonObject request = new JsonObject()
                .put("action", "addVote")
                .put("user_id", vote.getUser_id())
                .put("movie_id", movieId)
                .put("vote", vote.getVote());
        vertx.eventBus().request(Constants.CINE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.CREATED, ar.result().body());
            else EventBusUtil.handleReplyError(ctx, ar.cause(), "Error adding vote for this movie");
        });
    }

    public void deleteVote(RoutingContext ctx) {
        VoteEntity vote = Constants.GSON.fromJson(ctx.body().asString(), VoteEntity.class);
        JsonObject request = new JsonObject()
                .put("action", "deleteVote")
                .put("user_id", vote.getUser_id());
        vertx.eventBus().request(Constants.CINE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null);
            else EventBusUtil.handleReplyError(ctx, ar.cause(), "Error deleting vote for this movie");
        });
    }

    public void getVoteSelf(RoutingContext ctx) {
        String token = ctx.request().getHeader("Authorization").substring("Bearer ".length());
        JsonObject request = new JsonObject().put("action", "getVoteSelf").put("token", token);
        vertx.eventBus().request(Constants.CINE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            else EventBusUtil.handleReplyError(ctx, ar.cause(), "No vote found for this viewer");
        });
    }
}
