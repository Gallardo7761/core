package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.dao.PreUserDAO;
import net.miarma.api.huertos.entities.PreUserEntity;
import net.miarma.api.util.MessageUtil;

@SuppressWarnings("unused")
public class PreUserService {

	private final PreUserDAO preUserDAO;

	public PreUserService(Pool pool) {
		this.preUserDAO = new PreUserDAO(pool);
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

	public Future<PreUserEntity> create(PreUserEntity preUser) {
		return preUserDAO.insert(preUser);
	}

	public Future<PreUserEntity> update(PreUserEntity preUser) {
		return getById(preUser.getPre_user_id()).compose(existing -> {
			return preUserDAO.update(preUser);
		});
	}

	public Future<PreUserEntity> delete(Integer id) {
		return getById(id).compose(existing -> {
			return preUserDAO.delete(id);
		});
	}
}
