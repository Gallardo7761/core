package net.miarma.api.core.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.exceptions.AlreadyExistsException;
import net.miarma.api.common.exceptions.BadRequestException;
import net.miarma.api.common.exceptions.ForbiddenException;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.UnauthorizedException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.common.security.PasswordHasher;
import net.miarma.api.core.dao.UserDAO;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.validators.UserValidator;

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
                return getByUserName(emailOrUsername);
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
        return userDAO.getAll().compose(users -> {
            UserEntity found = users.stream()
                .filter(user -> user.getUser_id().equals(id))
                .findFirst()
                .orElse(null);
            return Future.succeededFuture(found);
        });
    }

    public Future<UserEntity> getByEmail(String email) {
        return userDAO.getAll().compose(users -> {
            UserEntity found = users.stream()
        		.filter(user -> user.getEmail() != null && user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
            return Future.succeededFuture(found);
        });
    }

    public Future<UserEntity> getByUserName(String userName) {
        return userDAO.getAll().compose(users -> {
            UserEntity found = users.stream()
                .filter(user -> user.getUser_name() != null && user.getUser_name().equals(userName))
                .findFirst()
                .orElse(null);
            return Future.succeededFuture(found);
        });
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
			return userDAO.update(user);
		});
    }

    public Future<UserEntity> delete(Integer id) {
        return getById(id).compose(user -> {
            if (user == null) {
                return Future.failedFuture(new NotFoundException("User not found in the database"));
            }
            return userDAO.delete(id);
        });
    }
}