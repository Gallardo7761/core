package net.miarma.api.microservices.huertosdecine.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertosdecine.entities.MovieEntity;
import net.miarma.api.microservices.huertosdecine.services.MovieService;
import net.miarma.api.util.JsonUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MovieDataHandler {
    private MovieService movieService;

    public MovieDataHandler(Pool pool) {
        this.movieService = new MovieService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        movieService.getAll(params)
            .onSuccess(movies -> JsonUtil.sendJson(ctx, ApiStatus.OK, movies))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer movieId = Integer.parseInt(ctx.pathParam("movie_id"));

        movieService.getById(movieId)
            .onSuccess(movie -> JsonUtil.sendJson(ctx, ApiStatus.OK, movie))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        MovieEntity movie = Constants.GSON.fromJson(ctx.body().asString(), MovieEntity.class);

        movieService.create(movie)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        MovieEntity movieFromBody = Constants.GSON.fromJson(ctx.body().asString(), MovieEntity.class);

        movieService.getById(movieFromBody.getMovie_id())
            .onSuccess(existingMovie -> {
                String newCover = movieFromBody.getCover();
                String oldCover = existingMovie.getCover();

                if (newCover != null && !newCover.isEmpty() && !newCover.equals(oldCover)) {
                    String cineFiles = ConfigManager.getInstance().getFilesDir("cine");
                    String filename = Paths.get(existingMovie.getCover()).getFileName().toString();
                    Path fullPath = Paths.get(cineFiles, filename);

                    ctx.vertx().fileSystem().delete(fullPath.toString(), fileRes -> {
                        if (fileRes.failed()) {
                            Constants.LOGGER.warn("No se pudo eliminar el archivo de portada: " + fullPath, fileRes.cause());
                        }

                        movieService.update(movieFromBody)
                                .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
                                .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
                    });
                } else {
                    movieService.update(movieFromBody)
                            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
                            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
                }
            })
            .onFailure(err -> {
                JsonUtil.sendJson(ctx, ApiStatus.NOT_FOUND, null, "Película no encontrada");
            });
    }

    public void delete(RoutingContext ctx) {
        Integer movieId = Integer.parseInt(ctx.pathParam("movie_id"));

        movieService.getById(movieId).onSuccess(movie -> {
            String cineFiles = ConfigManager.getInstance().getFilesDir("cine");
            String filename = Paths.get(movie.getCover()).getFileName().toString();
            Path fullPath = Paths.get(cineFiles, filename);

            ctx.vertx().fileSystem().delete(fullPath.toString(), fileRes -> {
                if (fileRes.failed()) {
                    Constants.LOGGER.warn("No se pudo eliminar el archivo de portada: " + fullPath, fileRes.cause());
                }

                movieService.delete(movieId)
                        .onSuccess(_ -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
                        .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
            });
        }).onFailure(err -> {
            JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage());
        });
    }
}
