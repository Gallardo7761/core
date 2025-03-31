package net.miarma.core.sso.services;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.security.JWTUtil;
import net.miarma.core.common.security.PasswordHasher;
import net.miarma.core.sso.dao.UserDAO;
import net.miarma.core.sso.entities.UserEntity;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService(Pool pool) {
        this.userDAO = new UserDAO(pool);
    }

    public void login(String email, String plainPassword, Handler<AsyncResult<JsonObject>> handler) {
        userDAO.findByEmail(email, ar -> {
            if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            UserEntity user = ar.result();
            if (user == null || !PasswordHasher.verify(plainPassword, user.getPassword())) {
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

    public void register(UserEntity user, Handler<AsyncResult<Void>> handler) {
        user.setPassword(PasswordHasher.hash(user.getPassword()));
        userDAO.insert(user, handler);
    }

    public void changePassword(int userId, String newPassword, Handler<AsyncResult<Void>> handler) {
        String hashed = PasswordHasher.hash(newPassword);
        userDAO.updatePassword(userId, hashed, handler);
    }

    public boolean validateToken(String token) {
        return JWTUtil.isValid(token);
    }
}