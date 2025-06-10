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

    public VoteService(Pool pool) {
        this.voteDAO = new VoteDAO(pool);
    }

    public Future<List<VoteEntity>> getAll(QueryParams params) {
        return voteDAO.getAll(params);
    }

    public Future<VoteEntity> getByUserId(Integer userId) {
        return voteDAO.getById(userId);
    }

    public Future<List<VoteEntity>> getVotesByMovieId(Integer movieId) {
        return voteDAO.getAll().compose(list -> {
           List<VoteEntity> votes = list.stream()
                .filter(v -> v.getMovie_id().equals(movieId))
                .toList();
            return Future.succeededFuture(votes);
        });
    }

    public Future<VoteEntity> create(VoteEntity vote) {
        return voteDAO.upsert(vote);
    }

    public Future<VoteEntity> delete(Integer userId) {
        return getByUserId(userId).compose(existing -> {
            if (existing == null) {
                return Future.failedFuture(new NotFoundException("Vote not found in the database"));
            }
            Integer movieId = existing.getMovie_id();
            return voteDAO.delete(movieId);
        });
    }

    public Future<VoteEntity> getVoteSelf(String token) {
        Integer userId = JWTManager.getInstance().getUserId(token);
        return voteDAO.getById(userId);
    }


}
