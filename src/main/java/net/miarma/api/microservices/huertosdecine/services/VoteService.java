package net.miarma.api.microservices.huertosdecine.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.microservices.huertosdecine.dao.VoteDAO;
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
        return voteDAO.getVotesByMovieId(movieId).compose(list -> {
            if (list.isEmpty()) {
                return Future.failedFuture(new NotFoundException("No votes found for the specified movie ID"));
            }
            return Future.succeededFuture(list);
        });
    }

    public Future<VoteEntity> create(VoteEntity vote) {
        return voteDAO.upsert(vote);
    }

    public Future<Boolean> delete(Integer userId) {
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
