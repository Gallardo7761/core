package net.miarma.api.miarmacraft.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.miarmacraft.dao.ModDAO;
import net.miarma.api.miarmacraft.entities.ModEntity;

public class ModService {
	private ModDAO modDAO;
	
	public ModService(Pool pool) {
		this.modDAO = new ModDAO(pool);
	}
	
	public Future<List<ModEntity>> getAll() {
		return modDAO.getAll();
	}
	
	public Future<List<ModEntity>> getAll(QueryParams params) {
		return modDAO.getAll(params);
	}
	
	public Future<ModEntity> getById(Integer id) {
		return getAll().compose(mods -> {
			ModEntity mod = mods.stream()
	                .filter(m -> m.getMod_id().equals(id))
	                .findFirst()
	                .orElse(null);
	            return mod != null ?
	                Future.succeededFuture(mod) :
	                Future.failedFuture(new NotFoundException("Mod with id " + id));
		});
	}
	
	public Future<ModEntity> update(ModEntity mod) {
		return modDAO.update(mod).compose(updatedMod -> {
			if (updatedMod == null) {
				return Future.failedFuture(new NotFoundException("Mod with id " + mod.getMod_id()));
			}
			return Future.succeededFuture(updatedMod);
		});
	}
	
	public Future<ModEntity> create(ModEntity mod) {
		return modDAO.insert(mod).compose(createdMod -> {
			return Future.succeededFuture(createdMod);
		});
	}
	
	public Future<Void> delete(Integer id) {
		return modDAO.delete(id).compose(deleted -> {
			if (deleted == null) {
				return Future.failedFuture(new NotFoundException("Mod with id " + id));
			}
			return Future.succeededFuture();
		});
	}
}
