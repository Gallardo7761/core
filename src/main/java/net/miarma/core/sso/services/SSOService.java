package net.miarma.core.sso.services;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.security.JWTUtil;
import net.miarma.core.common.security.PasswordHasher;
import net.miarma.core.sso.dao.UserDAO;
import net.miarma.core.sso.entities.UserEntity;
import net.miarma.core.util.MessageUtil;

public class SSOService {

    private final UserDAO userDAO;

    public SSOService(Pool pool) {
        this.userDAO = new UserDAO(pool);
    }

    /* AUTHENTICATION */

    public void login(String email, String plainPassword, Handler<AsyncResult<JsonObject>> handler) {
        getByEmail(email, ar -> {
            if (ar.failed() || ar.result() == null) {
                handler.handle(Future.failedFuture("Credenciales inválidas"));
                return;
            }

            UserEntity user = ar.result();
            if (!PasswordHasher.verify(plainPassword, user.getPassword())) {
                handler.handle(Future.failedFuture("Credenciales inválidas"));
            } else {
                String token = JWTUtil.generateToken(user.getUser_name(), user.getUser_id());
                JsonObject response = new JsonObject()
                    .put("token", token)
                    .put("user", JsonObject.mapFrom(user));
                handler.handle(Future.succeededFuture(response));
            }
        });
    }

    public void register(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
        getByEmail(user.getEmail(), ar -> {
            if (ar.succeeded() && ar.result() != null) {
                handler.handle(Future.failedFuture("Email ya registrado"));
                return;
            }

            user.setPassword(PasswordHasher.hash(user.getPassword()));
            userDAO.insert(user, handler);
        });
    }

    public void changePassword(int userId, String newPassword, Handler<AsyncResult<UserEntity>> handler) {
        getById(String.valueOf(userId), ar -> {
            if (ar.failed() || ar.result() == null) {
                handler.handle(Future.failedFuture(MessageUtil.notFound("User", "in the database")));
                return;
            }

            UserEntity user = ar.result();
            user.setPassword(PasswordHasher.hash(newPassword));
            userDAO.update(user, handler);
        });
    }

    public boolean validateToken(String token) {
        return JWTUtil.isValid(token);
    }

    /* USERS (Service lógica extra) */

    public void getAll(Handler<AsyncResult<List<UserEntity>>> handler) {
        userDAO.getAll(ar -> {
            if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }
            handler.handle(Future.succeededFuture(ar.result()));
        });
    }

    public void getById(String id, Handler<AsyncResult<UserEntity>> handler) {
        userDAO.getAll(ar -> {
            if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            ar.result().stream()
                .filter(user -> user.getUser_id().equals(Integer.parseInt(id)))
                .findFirst()
                .ifPresentOrElse(
                    user -> handler.handle(Future.succeededFuture(user)),
                    () -> handler.handle(Future.failedFuture(MessageUtil.notFound("User", "in the database")))
                );
        });
    }

    public void getByEmail(String email, Handler<AsyncResult<UserEntity>> handler) {
        userDAO.getAll(ar -> {
            if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            ar.result().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .ifPresentOrElse(
                    user -> handler.handle(Future.succeededFuture(user)),
                    () -> handler.handle(Future.succeededFuture(null))
                );
        });
    }

    public void getByUserName(String userName, Handler<AsyncResult<UserEntity>> handler) {
        userDAO.getAll(ar -> {
            if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            ar.result().stream()
                .filter(user -> userName.equals(user.getUser_name()))
                .findFirst()
                .ifPresentOrElse(
                    user -> handler.handle(Future.succeededFuture(user)),
                    () -> handler.handle(Future.succeededFuture(null))
                );
        });
    }
    
    public void updateRole(String userId, String role, Handler<AsyncResult<UserEntity>> handler) {
		getById(userId, ar -> {
			if (ar.failed() || ar.result() == null) {
				handler.handle(Future.failedFuture(MessageUtil.notFound("User", "in the database")));
				return;
			}

			UserEntity user = ar.result();
			user.setRole(Integer.parseInt(role));
			userDAO.update(user, handler);
		});
	}
    
    public void updateStatus(String userId, String status, Handler<AsyncResult<UserEntity>> handler) {
		getById(userId, ar -> {
			if(ar.failed() || ar.result() == null) {
				handler.handle(Future.failedFuture(MessageUtil.notFound("User", "in the database")));
				return;
			}
			
			UserEntity user = ar.result();
			user.setGlobal_status(Integer.parseInt(status));
			userDAO.update(user, handler);
		});
    }

    /* CRUD Básico */

    public void create(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
        register(user, handler); // Reutilizas lógica para validaciones
    }

    public void update(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
        userDAO.update(user, handler);
    }

    public void delete(String id, Handler<AsyncResult<UserEntity>> handler) {
        getById(id, ar -> {
            if (ar.failed() || ar.result() == null) {
                handler.handle(Future.failedFuture(MessageUtil.notFound("User", "in the database")));
                return;
            }
            userDAO.delete(Integer.parseInt(id), handler);
        });
    }
}
