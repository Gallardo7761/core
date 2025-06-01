package net.miarma.api.microservices.huertosdecine.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertosdecine.dao.MovieDAO;
import net.miarma.api.microservices.huertosdecine.entities.MovieEntity;

import java.util.List;

public class MovieService {
    private final MovieDAO movieDAO;

    public MovieService(Pool pool) {
        this.movieDAO = new MovieDAO(pool);
    }

    public Future<List<MovieEntity>> getAll(QueryParams params) {
        return movieDAO.getAll(params);
    }

    public Future<MovieEntity> getById(Integer id) {
        return movieDAO.getAll().compose(movies -> {
            MovieEntity movie = movies.stream()
                .filter(m -> m.getMovie_id().equals(id))
                .findFirst()
                .orElse(null);
            return Future.succeededFuture(movie);
        });
    }

    public Future<MovieEntity> create(MovieEntity movie) {
        return movieDAO.insert(movie);
    }

    public Future<MovieEntity> update(MovieEntity movie) {
        return getById(movie.getMovie_id()).compose(existing -> {
            if (existing == null) {
                return Future.failedFuture(new NotFoundException("Movie not found in the database"));
            }
            return movieDAO.update(movie);
        });
    }

    public Future<MovieEntity> delete(Integer id) {
        return getById(id).compose(existing -> {
            if (existing == null) {
                return Future.failedFuture(new NotFoundException("Movie not found in the database"));
            }
            return movieDAO.delete(id);
        });
    }
}
