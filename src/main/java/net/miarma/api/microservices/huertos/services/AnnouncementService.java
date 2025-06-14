package net.miarma.api.microservices.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.dao.AnnouncementDAO;
import net.miarma.api.microservices.huertos.entities.AnnouncementEntity;
import net.miarma.api.microservices.huertos.validators.AnnouncementValidator;

import java.util.List;

public class AnnouncementService {

	private final AnnouncementDAO announcementDAO;
	private final AnnouncementValidator announcementValidator;

	public AnnouncementService(Pool pool) {
		this.announcementDAO = new AnnouncementDAO(pool);
		this.announcementValidator = new AnnouncementValidator();
	}

	public Future<List<AnnouncementEntity>> getAll(QueryParams params) {
		return announcementDAO.getAll(params);
	}

	public Future<AnnouncementEntity> getById(Integer id) {
		return announcementDAO.getById(id).compose(announce -> {
			if (announce == null) {
				return Future.failedFuture(new NotFoundException("Announce not found in the database"));
			}
			return Future.succeededFuture(announce);
		});
	}

	public Future<AnnouncementEntity> create(AnnouncementEntity announce) {
		return announcementValidator.validate(announce).compose(validation -> {
			if (!validation.isValid()) {
			    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			return announcementDAO.insert(announce);
		});
	}

	public Future<AnnouncementEntity> update(AnnouncementEntity announce) {
		return getById(announce.getAnnounce_id()).compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(new NotFoundException("Announce not found in the database"));
			}
			return announcementValidator.validate(announce).compose(validation -> {
				if (!validation.isValid()) {
				    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return announcementDAO.update(announce);
			});
		});
	}

	public Future<Boolean> delete(Integer id) {
		return getById(id).compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(new NotFoundException("Announce not found in the database"));
			}
			return announcementDAO.delete(id);
		});
	}
}
