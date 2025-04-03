package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.QueryFilters;
import net.miarma.api.huertos.dao.PreUserDAO;
import net.miarma.api.huertos.entities.PreUserEntity;

public class PreUserService {

	private final PreUserDAO preUserDAO;

	public PreUserService(Pool pool) {
		this.preUserDAO = new PreUserDAO(pool);
	}

	public Future<List<PreUserEntity>> getAll(QueryFilters filters) {
		return preUserDAO.getAll(filters);
	}

	public Future<PreUserEntity> getById(Integer id) {
		return preUserDAO.getAll().compose(preUsers -> {
			PreUserEntity preUser = preUsers.stream()
				.filter(p -> p.getPre_user_id().equals(id))
				.findFirst()
				.orElse(null);
			return Future.succeededFuture(preUser);
		});
	}

	public Future<PreUserEntity> create(PreUserEntity preUser) {
		return preUserDAO.insert(preUser);
	}

	public Future<PreUserEntity> update(PreUserEntity preUser) {
		return preUserDAO.update(preUser);
	}

	public Future<PreUserEntity> delete(Integer id) {
		return preUserDAO.delete(id);
	}
}
