package net.miarma.api.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.dao.AnnounceDAO;
import net.miarma.api.huertos.entities.AnnounceEntity;
import net.miarma.api.huertos.validators.AnnounceValidator;

import java.util.List;

public class AnnounceService {

	private final AnnounceDAO announceDAO;
	private final AnnounceValidator announceValidator;

	public AnnounceService(Pool pool) {
		this.announceDAO = new AnnounceDAO(pool);
		this.announceValidator = new AnnounceValidator();
		
	}

	public Future<List<AnnounceEntity>> getAll(QueryParams params) {
		return announceDAO.getAll(params);
	}

	public Future<AnnounceEntity> getById(Integer id) {
		return announceDAO.getAll().compose(announces -> {
			AnnounceEntity announce = announces.stream()
				.filter(a -> a.getAnnounce_id().equals(id))
				.findFirst()
				.orElse(null);
			return Future.succeededFuture(announce);
		});
	}

	public Future<AnnounceEntity> create(AnnounceEntity announce) {
		return announceValidator.validate(announce).compose(validation -> {
			if (!validation.isValid()) {
			    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			return announceDAO.insert(announce);
		});
	}

	public Future<AnnounceEntity> update(AnnounceEntity announce) {
		return getById(announce.getAnnounce_id()).compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(new NotFoundException("Announce not found in the database"));
			}
			return announceValidator.validate(announce).compose(validation -> {
				if (!validation.isValid()) {
				    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return announceDAO.update(announce);
			});
		});
	}

	public Future<AnnounceEntity> delete(Integer id) {
		return getById(id).compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(new NotFoundException("Announce not found in the database"));
			}
			return announceDAO.delete(id);
		});
	}
}
