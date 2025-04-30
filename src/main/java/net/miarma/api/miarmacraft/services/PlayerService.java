package net.miarma.api.miarmacraft.services;

import java.util.List;

import com.auth0.jwt.interfaces.DecodedJWT;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.MMCUserRole;
import net.miarma.api.common.Constants.MMCUserStatus;
import net.miarma.api.common.exceptions.AlreadyExistsException;
import net.miarma.api.common.exceptions.BadRequestException;
import net.miarma.api.common.exceptions.ForbiddenException;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.common.security.PasswordHasher;
import net.miarma.api.core.dao.UserDAO;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.services.UserService;
import net.miarma.api.miarmacraft.dao.PlayerDAO;
import net.miarma.api.miarmacraft.dao.UserMetadataDAO;
import net.miarma.api.miarmacraft.entities.PlayerEntity;
import net.miarma.api.miarmacraft.entities.UserMetadataEntity;

public class PlayerService {
	private UserDAO userDAO;
	private PlayerDAO playerDAO;
	private UserMetadataDAO userMetadataDAO;
	private UserService userService;

	public PlayerService(Pool pool) {
		this.userDAO = new UserDAO(pool);
		this.playerDAO = new PlayerDAO(pool);
		this.userMetadataDAO = new UserMetadataDAO(pool);
		this.userService = new UserService(pool);
	}

	public Future<JsonObject> login(String emailOrUserName, String password, boolean keepLoggedIn) {
		return userService.login(emailOrUserName, password, keepLoggedIn).compose(json -> {
			JsonObject loggedUserJson = json.getJsonObject("loggedUser");
			UserEntity user = Constants.GSON.fromJson(loggedUserJson.encode(), UserEntity.class);

			if (user == null) {
				return Future.failedFuture(new BadRequestException("Invalid credentials"));
			}

			if (user.getGlobal_status() != Constants.CoreUserGlobalStatus.ACTIVE) {
				return Future.failedFuture(new ForbiddenException("User is not active"));
			}

			return userMetadataDAO.getAll().compose(metadataList -> {
				UserMetadataEntity userMetadata = metadataList.stream()
				    .filter(metadata -> metadata.getUser_id().equals(user.getUser_id()))
				    .findFirst()
				    .orElse(null);

				if (userMetadata == null) {
				    return Future.failedFuture(new NotFoundException("User metadata not found"));
				}

				if (userMetadata.getStatus() != MMCUserStatus.ACTIVE) {
				    return Future.failedFuture(new ForbiddenException("User is not active"));
				}

				PlayerEntity player = new PlayerEntity(user, userMetadata);

				return Future.succeededFuture(new JsonObject()
						.put("token", json.getString("token"))
						.put("loggedUser", new JsonObject(Constants.GSON.toJson(player)))
				);

			});
		});
	}

	public Future<List<PlayerEntity>> getAll() {
		return playerDAO.getAll();
	}

	public Future<List<PlayerEntity>> getAll(QueryParams queryParams) {
		return playerDAO.getAll(queryParams);
	}

	public Future<PlayerEntity> getById(Integer id) {
		return getAll().compose(players -> {
			return Future.succeededFuture(
					players.stream().filter(player -> player.getUser_id().equals(id)).findFirst().orElse(null));
		});
	}

	public Future<PlayerEntity> getByUserName(String username) {
		return getAll().compose(players -> {
			return Future.succeededFuture(
					players.stream().filter(player -> player.getUser_name().equals(username)).findFirst().orElse(null));
		});
	}

	public Future<PlayerEntity> getByEmail(String email) {
		return getAll().compose(players -> {
			return Future.succeededFuture(
					players.stream().filter(player -> player.getEmail().equals(email)).findFirst().orElse(null));
		});
	}

	public Future<MMCUserStatus> getStatus(Integer id) {
		return getById(id).compose(player -> {
			if (player == null) {
				return Future.failedFuture(new NotFoundException("Player not found"));
			}

			return Future.succeededFuture(player.getStatus());
		});
	}

	public Future<MMCUserRole> getRole(Integer id) {
		return getById(id).compose(player -> {
			if (player == null) {
				return Future.failedFuture(new NotFoundException("Player not found"));
			}

			return Future.succeededFuture(player.getRole());
		});
	}

	public Future<String> getAvatar(Integer id) {
		return getById(id).compose(player -> {
			if (player == null) {
				return Future.failedFuture(new NotFoundException("Player not found"));
			}

			return Future.succeededFuture(player.getAvatar());
		});
	}

	public Future<PlayerEntity> updateStatus(Integer id, MMCUserStatus status) {
		PlayerEntity player = new PlayerEntity();
		player.setUser_id(id);
		player.setStatus(status);
		return update(player).compose(updatedPlayer -> {
			return Future.succeededFuture(updatedPlayer);
		});
	}

	public Future<PlayerEntity> updateRole(Integer id, MMCUserRole role) {
		PlayerEntity player = new PlayerEntity();
		player.setUser_id(id);
		player.setRole(role);
		return update(player).compose(updatedPlayer -> {
			return Future.succeededFuture(updatedPlayer);
		});
	}

	public Future<PlayerEntity> updateAvatar(Integer id, String avatar) {
		PlayerEntity player = new PlayerEntity();
		player.setUser_id(id);
		player.setAvatar(avatar);
		return update(player).compose(updatedPlayer -> {
			return Future.succeededFuture(updatedPlayer);
		});
	}

	public Future<PlayerEntity> create(PlayerEntity player) {
		return getById(player.getUser_id()).compose(existingPlayer -> {
			if (existingPlayer != null) {
				return Future.failedFuture(new AlreadyExistsException("Player already exists"));
			}

			player.setPassword(PasswordHasher.hash(player.getPassword()));

			return userDAO.insert(UserEntity.fromPlayerEntity(player)).compose(user -> {
				UserMetadataEntity metadata = UserMetadataEntity.fromPlayerEntity(player);
				metadata.setUser_id(user.getUser_id());
				return userMetadataDAO.insert(metadata).map(_ -> {
					return player;
				});
			});
		});
	}

	public Future<PlayerEntity> update(PlayerEntity player) {
		return getById(player.getUser_id()).compose(existingPlayer -> {
			if (existingPlayer == null) {
				return Future.failedFuture(new NotFoundException("Player does not exist"));
			}

			return userDAO.update(UserEntity.fromPlayerEntity(player)).compose(user -> {
				UserMetadataEntity userMetadata = UserMetadataEntity.fromPlayerEntity(player);
				return userMetadataDAO.update(userMetadata).map(_ -> player);
			});
		});
	}

	public Future<PlayerEntity> delete(Integer id) {
		return getById(id).compose(existingPlayer -> {
			if (existingPlayer == null) {
				return Future.failedFuture(new NotFoundException("Player does not exist"));
			}

			return userDAO.delete(id).compose(_ -> {
				return userMetadataDAO.delete(id).map(_ -> existingPlayer);
			});
		});
	}
	
	public Future<Boolean> playerExists(Integer id) {
		return getById(id).compose(player -> {
			if (player == null) {
				return Future.succeededFuture(false);
			}
			return Future.succeededFuture(true);
		});
	}
	
	public Future<PlayerEntity> getInfo(String token) {
		Integer userId = JWTManager.getInstance().getUserId(token);
		return getById(userId).compose(player -> {
			if (player == null) {
				return Future.failedFuture(new NotFoundException("Player not found"));
			}

			return Future.succeededFuture(player);
		});
	}
}
