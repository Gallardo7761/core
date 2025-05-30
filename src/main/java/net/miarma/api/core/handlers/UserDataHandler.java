package net.miarma.api.core.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.services.UserService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class UserDataHandler {

    private final UserService userService;

    public UserDataHandler(Pool pool) {
        this.userService = new UserService(pool);
    }

    public void getAll(RoutingContext ctx) {
    	QueryParams params = QueryParams.from(ctx);
    	
        userService.getAll(params)
	        .onSuccess(users -> {
	            JsonUtil.sendJson(ctx, ApiStatus.OK, users);
	        }).onFailure(err -> {
	        	ApiStatus status = ApiStatus.fromException(err);
	        	JsonUtil.sendJson(ctx, status, null, err.getMessage());
	        });
    }

    public void getById(RoutingContext ctx) {
        Integer userId = Integer.parseInt(ctx.pathParam("user_id"));
        
        userService.getById(userId)
	        .onSuccess(user -> {
	            JsonUtil.sendJson(ctx, ApiStatus.OK, user);
	        }).onFailure(err -> {
	            ApiStatus status = ApiStatus.fromException(err);
				JsonUtil.sendJson(ctx, status, null, err.getMessage());
	        });
    }

    public void create(RoutingContext ctx) {
        UserEntity user = Constants.GSON.fromJson(ctx.body().asString(), UserEntity.class);
        
        userService.register(user)
	        .onSuccess(result -> {
	            JsonUtil.sendJson(ctx, ApiStatus.CREATED, result);
	        }).onFailure(err -> {
	            ApiStatus status = ApiStatus.fromException(err);
				JsonUtil.sendJson(ctx, status, null, err.getMessage());
	        });
    }

    public void update(RoutingContext ctx) {
        UserEntity user = Constants.GSON.fromJson(ctx.body().asString(), UserEntity.class);
        
        userService.update(user)
	        .onSuccess(result -> {
	            JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, result);
	        }).onFailure(err -> {
	            ApiStatus status = ApiStatus.fromException(err);
				JsonUtil.sendJson(ctx, status, null, err.getMessage());
	        });
    }

    public void delete(RoutingContext ctx) {
        Integer userId = Integer.parseInt(ctx.pathParam("user_id"));
        
        userService.delete(userId)
	        .onSuccess(result -> {
	            JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, result);
	        }).onFailure(err -> {
	            ApiStatus status = ApiStatus.fromException(err);
				JsonUtil.sendJson(ctx, status, null, err.getMessage());
	        });
    }
} 