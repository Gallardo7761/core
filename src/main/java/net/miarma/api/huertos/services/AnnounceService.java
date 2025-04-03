package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.QueryParams;
import net.miarma.api.huertos.dao.AnnounceDAO;
import net.miarma.api.huertos.entities.AnnounceEntity;

public class AnnounceService {
	
	private final AnnounceDAO announceDAO;
	
	public AnnounceService(Pool pool) {
		this.announceDAO = new AnnounceDAO(pool);
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
		return announceDAO.insert(announce);
	}
	
	public Future<AnnounceEntity> update(AnnounceEntity announce) {
		return announceDAO.update(announce);
	}
	
	public Future<AnnounceEntity> delete(Integer id) {
		return announceDAO.delete(id);
	}
	
}
