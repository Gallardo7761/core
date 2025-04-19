package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.dao.PreUserDAO;
import net.miarma.api.huertos.entities.PreUserEntity;
import net.miarma.api.huertos.validators.PreUserValidator;
import net.miarma.api.util.MessageUtil;

@SuppressWarnings("unused")
public class PreUserService {

	private final PreUserDAO preUserDAO;
	private final PreUserValidator preUserValidator;

	public PreUserService(Pool pool) {
		this.preUserDAO = new PreUserDAO(pool);
		this.preUserValidator = new PreUserValidator();
	}

	public Future<List<PreUserEntity>> getAll(QueryParams params) {
		return preUserDAO.getAll(params);
	}

	public Future<PreUserEntity> getById(Integer id) {
		return preUserDAO.getAll().compose(preUsers -> {
			PreUserEntity preUser = preUsers.stream()
				.filter(p -> p.getPre_user_id().equals(id))
				.findFirst()
				.orElse(null);

			if (preUser == null) {
				return Future.failedFuture(MessageUtil.notFound("PreUser", "with id " + id));
			}

			return Future.succeededFuture(preUser);
		});
	}
	
	public Future<PreUserEntity> getByRequestId(Integer requestId) {
		return preUserDAO.getAll().compose(preUsers -> {
			PreUserEntity preUser = preUsers.stream()
				.filter(p -> p.getRequest_id().equals(requestId))
				.findFirst()
				.orElse(null);

			if (preUser == null) {
				return Future.failedFuture(MessageUtil.notFound("PreUser", "with request id " + requestId));
			}

			return Future.succeededFuture(preUser);
		});
	}
	
	public Future<PreUserEntity> validatePreUser(String json) {
	    PreUserEntity preUser = Constants.GSON.fromJson(json, PreUserEntity.class);
	    return preUserValidator.validate(preUser, false).compose(validation -> {
	        if (!validation.isValid()) {
	            return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
	        }
	        return Future.succeededFuture(preUser);
	    });
	}
	
	public Future<PreUserEntity> create(PreUserEntity preUser) {
		return preUserValidator.validate(preUser, true).compose(validation -> {
			if (!validation.isValid()) {
			    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			return preUserDAO.insert(preUser);
		});
	}
	
	public Future<PreUserEntity> update(PreUserEntity preUser) {
		return getById(preUser.getPre_user_id()).compose(existing -> {
			return preUserValidator.validate(preUser, true).compose(validation -> {
				if (!validation.isValid()) {
				    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return preUserDAO.update(preUser);
			});
		});
	}

	public Future<PreUserEntity> delete(Integer id) {
		return getById(id).compose(existing -> {
			return preUserDAO.delete(id);
		});
	}
}
