package net.miarma.api.miarmacraft.dao;

import java.util.List;

import io.vertx.core.Future;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.miarmacraft.entities.PlayerEntity;

public class PlayerDAO implements DataAccessObject<PlayerEntity> {

	@Override
	public Future<List<PlayerEntity>> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<PlayerEntity> insert(PlayerEntity t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<PlayerEntity> update(PlayerEntity t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<PlayerEntity> delete(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

}
