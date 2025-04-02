package net.miarma.api.core.services;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.common.security.PasswordHasher;
import net.miarma.api.core.dao.UserDAO;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.util.MessageUtil;

public class CoreService {

    private final UserDAO userDAO;
    public CoreService(Pool pool) {
        this.userDAO = new UserDAO(pool);
    }

    /* AUTHENTICATION */

    public void login(String email, String plainPassword, boolean keepLoggedIn, Handler<AsyncResult<JsonObject>> handler) {
        getByEmail(email, ar -> {
            if (ar.failed() || ar.result() == null) {
                handler.handle(Future.failedFuture("Invalid credentials"));
                return;
            }

            UserEntity user = ar.result();
                        
            if (user.getGlobal_status() != CoreUserGlobalStatus.ACTIVE) {
				handler.handle(Future.failedFuture("User is not active or banned"));
				return;
			}
            
            if (!PasswordHasher.verify(plainPassword, user.getPassword())) {
                handler.handle(Future.failedFuture("Invalid credentials"));
                return;
            }
            
            JWTManager jwtManager = JWTManager.getInstance();
            String token = jwtManager.generateToken(user, keepLoggedIn);
            JsonObject response = new JsonObject()
                .put("token", token)
                .put("loggedUser", new JsonObject(user.encode()));
            handler.handle(Future.succeededFuture(response));
        });
    }

    public void register(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
        getByEmail(user.getEmail(), ar -> {
            if (ar.succeeded() && ar.result() != null) {
                handler.handle(Future.failedFuture("Email already exists"));
                return;
            }

            user.setPassword(PasswordHasher.hash(user.getPassword()));
            user.setRole(CoreUserRole.USER);
            user.setGlobal_status(CoreUserGlobalStatus.ACTIVE);
            userDAO.insert(user, handler);
        });
    }

    public void changePassword(int userId, String newPassword, Handler<AsyncResult<UserEntity>> handler) {
        getById(userId, ar -> {
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
    	JWTManager jwtManager = JWTManager.getInstance();
        return jwtManager.isValid(token);
    }

    /* USERS OPERATIONS */

    public void getAll(Handler<AsyncResult<List<UserEntity>>> handler) {
        userDAO.getAll(ar -> {
            if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }
            handler.handle(Future.succeededFuture(ar.result()));
        });
    }

    public void getById(Integer id, Handler<AsyncResult<UserEntity>> handler) {
        userDAO.getAll(ar -> {
            if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            ar.result().stream()
                .filter(user -> user.getUser_id().equals(id))
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
    
    public void updateRole(Integer userId, CoreUserRole role, Handler<AsyncResult<UserEntity>> handler) {
		getById(userId, ar -> {
			if (ar.failed() || ar.result() == null) {
				handler.handle(Future.failedFuture(MessageUtil.notFound("User", "in the database")));
				return;
			}

			UserEntity user = ar.result();
			user.setRole(role);
			userDAO.update(user, handler);
		});
	}
    
    public void updateStatus(Integer userId, CoreUserGlobalStatus status, Handler<AsyncResult<UserEntity>> handler) {
		getById(userId, ar -> {
			if(ar.failed() || ar.result() == null) {
				handler.handle(Future.failedFuture(MessageUtil.notFound("User", "in the database")));
				return;
			}
			
			UserEntity user = ar.result();
			user.setGlobal_status(status);
			userDAO.update(user, handler);
		});
    }
    
    /* CRUD OPERATIONS */

    public void create(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
        register(user, handler);
    }

    public void update(UserEntity user, Handler<AsyncResult<UserEntity>> handler) {
        userDAO.update(user, handler);
    }

    public void delete(Integer id, Handler<AsyncResult<UserEntity>> handler) {
        getById(id, ar -> {
            if (ar.failed() || ar.result() == null) {
                handler.handle(Future.failedFuture(MessageUtil.notFound("User", "in the database")));
                return;
            }
            userDAO.delete(id, handler);
        });
    }
}
