package net.miarma.api.microservices.core.services;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.exceptions.*;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.common.security.PasswordHasher;
import net.miarma.api.microservices.core.dao.UserDAO;
import net.miarma.api.microservices.core.entities.UserEntity;
import net.miarma.api.microservices.core.validators.UserValidator;

import java.util.List;

public class UserService {

    private final UserDAO userDAO;
    private final UserValidator userValidator;

    public UserService(Pool pool) {
    	this.userDAO = new UserDAO(pool);
        this.userValidator = new UserValidator();
    }

    /* AUTHENTICATION */

    public Future<JsonObject> login(String emailOrUsername, String plainPassword, boolean keepLoggedIn) {
        return getByEmail(emailOrUsername).compose(user -> {
            if (user == null) {
                return getByUserName(emailOrUsername).compose(user2 -> {
                    if (user2 == null) {
                        return Future.succeededFuture(null);
                    }
                    return Future.succeededFuture(user2);
                });
            }
            return Future.succeededFuture(user);
        }).compose(user -> {
        	
        	if (user == null) {
				return Future.failedFuture(new BadRequestException("Invalid credentials"));
			}
            
            if (user.getGlobal_status() != Constants.CoreUserGlobalStatus.ACTIVE) {
            	return Future.failedFuture(new ForbiddenException("User is not active"));
            }

            if (!PasswordHasher.verify(plainPassword, user.getPassword())) {
                return Future.failedFuture(new BadRequestException("Invalid credentials"));
            }

            JWTManager jwtManager = JWTManager.getInstance();
            String token = jwtManager.generateToken(user, keepLoggedIn);

            JsonObject response = new JsonObject()
                .put("token", token)
                .put("loggedUser", new JsonObject(user.encode()));

            return Future.succeededFuture(response);
        });
    }
    
    public Future<JsonObject> loginValidate(Integer userId, String password) {
		return getById(userId).compose(user -> {
			if (user == null) {
				return Future.failedFuture(new NotFoundException("User not found"));
			}
			if (!PasswordHasher.verify(password, user.getPassword())) {
				return Future.failedFuture(new BadRequestException("Invalid credentials"));
			}
			JsonObject response = new JsonObject()
				.put("valid", true);
			return Future.succeededFuture(response);
		});
	}

    public Future<UserEntity> register(UserEntity user) {
        return getByEmail(user.getEmail()).compose(existing -> {
            if (existing != null) {
                return Future.failedFuture(new AlreadyExistsException("Email already exists"));
            }

            user.setPassword(PasswordHasher.hash(user.getPassword()));
            user.setRole(CoreUserRole.USER);
            user.setGlobal_status(CoreUserGlobalStatus.ACTIVE);
            
            return userValidator.validate(user).compose(validation -> {
				if (!validation.isValid()) {
					return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return userDAO.insert(user);
			});
        });
    }

    public Future<UserEntity> changePassword(int userId, String newPassword) {
        return getById(userId).compose(user -> {
            if (user == null) {
                return Future.failedFuture(new NotFoundException("User not found"));
            }

            user.setPassword(PasswordHasher.hash(newPassword));
            return userDAO.update(user);
        });
    }

    public Future<Boolean> validateToken(String token) {
        JWTManager jwtManager = JWTManager.getInstance();
        return jwtManager.isValid(token) ?
            Future.succeededFuture(true) :
            Future.failedFuture(new UnauthorizedException("Invalid token"));
    }

    /* USERS OPERATIONS */

    public Future<List<UserEntity>> getAll(QueryParams params) {
        return userDAO.getAll(params);
    }

    public Future<UserEntity> getById(Integer id) {
        return userDAO.getById(id).compose(user -> {
            if (user == null) {
                return Future.failedFuture(new NotFoundException("User not found in the database"));
            }
            return Future.succeededFuture(user);
        });
    }

    public Future<UserEntity> getByEmail(String email) {
        return userDAO.getByEmail(email);
    }

    public Future<UserEntity> getByUserName(String userName) {
        return userDAO.getByUserName(userName);
    }

    public Future<UserEntity> updateRole(Integer userId, CoreUserRole role) {
        return getById(userId).compose(user -> {
            if (user == null) {
                return Future.failedFuture(new NotFoundException("User not found in the database"));
            }
            user.setRole(role);
            return userDAO.update(user);
        });
    }

    public Future<UserEntity> updateStatus(Integer userId, CoreUserGlobalStatus status) {
        return getById(userId).compose(user -> {
            if (user == null) {
                return Future.failedFuture(new NotFoundException("User not found in the database"));
            }
            user.setGlobal_status(status);
            return userDAO.update(user);
        });
    }

    /* CRUD OPERATIONS */

    public Future<UserEntity> create(UserEntity user) {
        return userValidator.validate(user).compose(validation -> {
			if (!validation.isValid()) {
				return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			return userDAO.insert(user);
		});
    }

    public Future<UserEntity> update(UserEntity user) {
    	return userValidator.validate(user).compose(validation -> {
			if (!validation.isValid()) {
				return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			if (user.getPassword() == null || user.getPassword().isEmpty()) {
				user.setPassword(null);
			}
			return userDAO.update(user);
		});
    }

    public Future<UserEntity> upsert(UserEntity user) {
        return userValidator.validate(user).compose(validation -> {
            if (!validation.isValid()) {
                return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
            }
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(PasswordHasher.hash(user.getPassword()));
            }
            return userDAO.upsert(user, "user_id", "email", "user_name");
        });
    }

    public Future<Boolean> delete(Integer id) {
        return getById(id).compose(user -> {
            if (user == null) {
                return Future.failedFuture(new NotFoundException("User not found in the database"));
            }
            return userDAO.delete(id);
        });
    }
}