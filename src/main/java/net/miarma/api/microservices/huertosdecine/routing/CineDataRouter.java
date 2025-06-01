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
import net.miarma.api.microservices.huertosdecine.handlers.MovieDataHandler;
import net.miarma.api.microservices.huertosdecine.handlers.ViewerDataHandler;
import net.miarma.api.microservices.huertosdecine.services.ViewerService;

public class CineDataRouter {
    public static void mount(Router router, Vertx vertx, Pool pool) {
        MovieDataHandler hMovieData = new MovieDataHandler(pool);
        ViewerDataHandler hViewerData = new ViewerDataHandler(pool);
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

        router.get(CineEndpoints.MOVIES).handler(AuthGuard.check()).handler(hMovieData::getAll);
        router.get(CineEndpoints.MOVIE).handler(AuthGuard.check()).handler(hMovieData::getById);
        router.post(CineEndpoints.MOVIES).handler(AuthGuard.cineAdmin(viewerService)).handler(hMovieData::create);
        router.put(CineEndpoints.MOVIE).handler(AuthGuard.cineAdmin(viewerService)).handler(hMovieData::update);
        router.delete(CineEndpoints.MOVIE).handler(AuthGuard.cineAdmin(viewerService)).handler(hMovieData::delete);

        router.get(CineEndpoints.VIEWERS).handler(AuthGuard.cineAdmin(viewerService)).handler(hViewerData::getAll);
        router.get(CineEndpoints.VIEWER).handler(AuthGuard.cineAdmin(viewerService)).handler(hViewerData::getById);
        router.post(CineEndpoints.VIEWERS).handler(AuthGuard.cineAdmin(viewerService)).handler(hViewerData::create);
        router.put(CineEndpoints.VIEWER).handler(AuthGuard.cineAdmin(viewerService)).handler(hViewerData::update);
        router.delete(CineEndpoints.VIEWER).handler(AuthGuard.cineAdmin(viewerService)).handler(hViewerData::delete);
    }
}
