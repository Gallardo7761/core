package net.miarma.api.microservices.huertosdecine.routing;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.common.security.SusPather;
import net.miarma.api.microservices.huertosdecine.handlers.ViewerLogicHandler;
import net.miarma.api.microservices.huertosdecine.handlers.VoteLogicHandler;
import net.miarma.api.microservices.huertosdecine.services.ViewerService;

public class CineLogicRouter {
    public static void mount(Router router, Vertx vertx, Pool pool) {
        ViewerLogicHandler hViewerLogic = new ViewerLogicHandler(vertx);
        VoteLogicHandler hVoteLogic = new VoteLogicHandler(vertx);
        ViewerService viewerService = new ViewerService(pool);

        router.route().handler(BodyHandler.create());
        // teapot :P
        router.route().handler(ctx -> {
            String path = ctx.request().path();
            ApiResponse<JsonObject> response = new ApiResponse<JsonObject>(ApiStatus.IM_A_TEAPOT, "I'm a teapot", null);
            JsonObject jsonResponse = new JsonObject().put("status", response.getStatus()).put("message",
                    response.getMessage());
            if (SusPather.isSusPath(path)) {
                ctx.response().setStatusCode(response.getStatus()).putHeader("Content-Type", "application/json")
                        .end(jsonResponse.encode());
            } else {
                ctx.next();
            }
        });

        router.post(CineEndpoints.LOGIN).handler(hViewerLogic::login);
        router.get(CineEndpoints.VIEWER_VOTES_BY_MOVIE).handler(AuthGuard.cineAdmin(viewerService)).handler(hViewerLogic::getVotesOnMovieByUserId);
        router.get(CineEndpoints.MOVIE_VOTES).handler(AuthGuard.check()).handler(hVoteLogic::getVotes);
        router.post(CineEndpoints.MOVIE_VOTES).handler(AuthGuard.check()).handler(hVoteLogic::addVote);
        router.delete(CineEndpoints.MOVIE_VOTES).handler(AuthGuard.check()).handler(hVoteLogic::deleteVote);
        router.get(CineEndpoints.SELF_VOTES).handler(AuthGuard.check()).handler(hVoteLogic::getVoteSelf);
    }
}
