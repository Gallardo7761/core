package net.miarma.api.microservices.huertosdecine.services;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.microservices.huertosdecine.dao.VoteDAO;
import net.miarma.api.microservices.huertosdecine.entities.MovieEntity;
import net.miarma.api.microservices.huertosdecine.entities.VoteEntity;

import java.util.List;

public class VoteService {
    private final VoteDAO voteDAO;
    private final ViewerService viewerService;

    public VoteService(Pool pool) {
        this.voteDAO = new VoteDAO(pool);
        this.viewerService = new ViewerService(pool);
    }

    public Future<List<VoteEntity>> getAll(QueryParams params) {
        return voteDAO.getAll(params);
    }

    public Future<VoteEntity> getByUserAndMovie(Integer userId, Integer movieId) {
        return voteDAO.getAll().compose(list -> {
            VoteEntity vote = list.stream()
                    .filter(v -> v.getUser_id().equals(userId) && v.getMovie_id().equals(movieId))
                    .findFirst()
                    .orElse(null);
            return Future.succeededFuture(vote);
        });
    }

    public Future<List<VoteEntity>> getVotesByMovieId(Integer movieId) {
        return voteDAO.getAll().compose(list -> {
           List<VoteEntity> votes = list.stream()
                .filter(v -> v.getMovie_id().equals(movieId))
                .toList();
            return Future.succeededFuture(votes);
        });
    }

    public Future<List<VoteEntity>> getVotesByUserAndMovieId(Integer userId, Integer movieId) {
        return voteDAO.getAll().compose(list -> {
            List<VoteEntity> votes = list.stream()
                    .filter(v -> v.getUser_id().equals(userId) && v.getMovie_id().equals(movieId))
                    .toList();
            return Future.succeededFuture(votes);
        });
    }

    public Future<VoteEntity> getVoteSelf(String token, Integer movieId) {
        Integer userId = JWTManager.getInstance().getUserId(token);
        return getByUserAndMovie(userId, movieId).compose(existing -> {
            if (existing == null) {
                return Future.failedFuture(new NotFoundException("Vote not found for this user and movie"));
            }
            return Future.succeededFuture(existing);
        });
    }

    public Future<VoteEntity> create(VoteEntity vote) {
        return voteDAO.insert(vote);
    }

    public Future<VoteEntity> update(VoteEntity vote) {
        return getByUserAndMovie(vote.getUser_id(), vote.getMovie_id()).compose(existing -> {
            if (existing == null) {
                return Future.failedFuture(new NotFoundException("Vote not found in the database"));
            }
            return voteDAO.update(vote);
        });
    }

    public Future<VoteEntity> delete(Integer userId, Integer movieId) {
        return getByUserAndMovie(userId, movieId).compose(existing -> {
            if (existing == null) {
                return Future.failedFuture(new NotFoundException("Vote not found in the database"));
            }
            return voteDAO.deleteDoubleId(userId, movieId);
        });
    }


}
