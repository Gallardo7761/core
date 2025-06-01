package net.miarma.api.microservices.huertosdecine.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.microservices.huertosdecine.entities.VoteEntity;
import net.miarma.api.microservices.huertosdecine.routing.CineDataRouter;
import net.miarma.api.microservices.huertosdecine.services.MovieService;
import net.miarma.api.microservices.huertosdecine.services.ViewerService;
import net.miarma.api.microservices.huertosdecine.services.VoteService;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.RouterUtil;

public class CineDataVerticle extends AbstractVerticle {
    private ConfigManager configManager;
    private MovieService movieService;
    private VoteService voteService;
    private ViewerService viewerService;

    @Override
    public void start(Promise<Void> startPromise) {
        configManager = ConfigManager.getInstance();
        Pool pool = DatabaseProvider.createPool(vertx, configManager);

        movieService = new MovieService(pool);
        voteService = new VoteService(pool);
        viewerService = new ViewerService(pool);

        Router router = Router.router(vertx);
        RouterUtil.attachLogger(router);
        CineDataRouter.mount(router, vertx, pool);
        registerLogicVerticleConsumer();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(configManager.getIntProperty("cine.data.port"), res -> {
                if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
            });
    }

    private void registerLogicVerticleConsumer() {
        vertx.eventBus().consumer(Constants.CINE_EVENT_BUS, message -> {
            JsonObject body = (JsonObject) message.body();
            String action = body.getString("action");

            switch (action) {
                case "login" -> {
                    String email = body.getString("email", null);
                    String userName = body.getString("userName", null);
                    String password = body.getString("password");
                    boolean keepLoggedIn = body.getBoolean("keepLoggedIn", false);

                    viewerService.login(email != null ? email : userName, password, keepLoggedIn)
                        .onSuccess(message::reply)
                        .onFailure(EventBusUtil.fail(message));
                }

                case "getVoteSelf" -> {
                    String token = body.getString("token");
                    Integer movieId = body.getInteger("movie_id");
                    voteService.getVoteSelf(token, movieId)
                        .onSuccess(vote -> message.reply(new JsonObject(Constants.GSON.toJson(vote))))
                        .onFailure(EventBusUtil.fail(message));
                }

                case "getVotesOnMovieByUserId" -> {
                    Integer userId = body.getInteger("user_id");
                    Integer movieId = body.getInteger("movie_id");
                    voteService.getVotesByUserAndMovieId(userId, movieId)
                        .onSuccess(votes -> {
                            if (votes.isEmpty()) {
                                message.reply(new JsonObject().put("message", "No votes found for this movie and viewer"));
                            } else {
                                message.reply(new JsonObject(Constants.GSON.toJson(votes)));
                            }
                        })
                        .onFailure(EventBusUtil.fail(message));
                }

                case "getVotes" -> voteService.getVotesByMovieId(body.getInteger("movie_id"))
                    .onSuccess(votes -> message.reply(new JsonObject(Constants.GSON.toJson(votes))))
                    .onFailure(EventBusUtil.fail(message));

                case "addVote" -> {
                    VoteEntity vote = Constants.GSON.fromJson(body.encode(), VoteEntity.class);
                    voteService.create(vote)
                        .onSuccess(createdVote -> message.reply(new JsonObject(Constants.GSON.toJson(createdVote))))
                        .onFailure(EventBusUtil.fail(message));
                }

                case "updateVote" -> {
                    VoteEntity vote = Constants.GSON.fromJson(body.encode(), VoteEntity.class);
                    voteService.update(vote)
                        .onSuccess(updatedVote -> message.reply(new JsonObject(Constants.GSON.toJson(updatedVote))))
                        .onFailure(EventBusUtil.fail(message));
                }

                case "deleteVote" -> {
                    Integer userId = body.getInteger("user_id");
                    Integer movieId = body.getInteger("movie_id");
                    voteService.delete(userId, movieId)
                        .onSuccess(deletedVote -> message.reply(new JsonObject(Constants.GSON.toJson(deletedVote))))
                        .onFailure(EventBusUtil.fail(message));
                }

                default -> EventBusUtil.fail(message).handle(new IllegalArgumentException("Unknown action: " + action));
            }
        });
    }

}
