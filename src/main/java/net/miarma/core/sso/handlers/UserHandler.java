package net.miarma.core.sso.handlers;

import java.util.stream.Collectors;

import com.google.gson.Gson;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.SingleJsonResponse;
import net.miarma.core.common.security.JWTManager;
import net.miarma.core.sso.entities.UserEntity;
import net.miarma.core.sso.services.SSOService;

public class UserHandler {

	private final SSOService ssoService;
	private final Gson gson = new Gson();

	public UserHandler(Pool pool) {
		this.ssoService = new SSOService(pool);
	}

	public void getAll(RoutingContext ctx) {
		ssoService.getAll(ar -> {
			if (ar.succeeded()) {
				String result = ar.result().stream().map(UserEntity::encode)
						.collect(Collectors.joining(", ", "[", "]"));
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			} else {
				ctx.response().setStatusCode(500).end(gson.toJson(SingleJsonResponse.of("Internal server error")));
			}
		});
	}

	public void getById(RoutingContext ctx) {	
		ssoService.getById(ctx.pathParam("user_id"), ar -> {
			if (ar.succeeded()) {
				ctx.response().putHeader("Content-Type", "application/json").end(ar.result().encode().toString());
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void getByEmail(RoutingContext ctx) {
		ssoService.getByEmail(ctx.pathParam("email"), ar -> {
			if (ar.succeeded()) {
				ctx.response().putHeader("Content-Type", "application/json").end(ar.result().encode());
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void getByUserName(RoutingContext ctx) {
		ssoService.getByUserName(ctx.pathParam("user_name"), ar -> {
			if (ar.succeeded()) {
				ctx.response().putHeader("Content-Type", "application/json").end(ar.result().encode());
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void getStatus(RoutingContext ctx) {
		String userId = ctx.pathParam("user_id");

		ssoService.getById(userId, ar -> {
			if (ar.succeeded()) {
				UserEntity user = ar.result();

				JsonObject response = new JsonObject().put("user_id", user.getUser_id()).put("status",
						user.getGlobal_status());

				ctx.response().putHeader("Content-Type", "application/json").end(response.encode());
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void getRole(RoutingContext ctx) {
		String userId = ctx.pathParam("user_id");

		ssoService.getById(userId, ar -> {
			if (ar.succeeded()) {
				UserEntity user = ar.result();

				JsonObject response = new JsonObject().put("user_id", user.getUser_id()).put("roles", user.getRole());

				ctx.response().putHeader("Content-Type", "application/json").end(response.encode());
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void getInfo(RoutingContext ctx) {
	    String authHeader = ctx.request().getHeader("Authorization");

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        ctx.response().setStatusCode(401).end(gson.toJson(SingleJsonResponse.of("Unauthorized")));
	        return;
	    }

	    String token = authHeader.substring(7);
	    int userId = JWTManager.getInstance().getUserId(token);

	    if (userId <= 0) {
	        ctx.response().setStatusCode(401).end(gson.toJson(SingleJsonResponse.of("Invalid token")));
	        return;
	    }

	    ssoService.getById(String.valueOf(userId), ar -> {
	        if (ar.failed() || ar.result() == null) {
	            ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
	            return;
	        }

	        UserEntity user = ar.result();
	        ctx.response()
	            .putHeader("Content-Type", "application/json")
	            .end(user.encode());
	    });
	}


	public void exists(RoutingContext ctx) {
		String userId = ctx.pathParam("user_id");

		ssoService.getById(userId, ar -> {
			JsonObject response = new JsonObject().put("user_id", Integer.parseInt(userId)).put("exists",
					ar.succeeded() && ar.result() != null);

			ctx.response().putHeader("Content-Type", "application/json").end(response.encode());
		});
	}

	public void getAvatar(RoutingContext ctx) {
		String userId = ctx.pathParam("user_id");

		ssoService.getById(userId, ar -> {
			if (ar.succeeded()) {
				UserEntity user = ar.result();

				JsonObject response = new JsonObject().put("user_id", user.getUser_id()).put("avatar",
						user.getAvatar());

				ctx.response().putHeader("Content-Type", "application/json").end(response.encode());
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void create(RoutingContext ctx) {
		UserEntity user = gson.fromJson(ctx.body().asString(), UserEntity.class);
		ssoService.create(user, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(201).end(ar.result().encode());
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void updateRole(RoutingContext ctx) {
		String userId = ctx.pathParam("user_id");
		String role = ctx.request().getParam("role");
		ssoService.updateRole(userId, role, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void update(RoutingContext ctx) {
		UserEntity user = gson.fromJson(ctx.body().asString(), UserEntity.class);
		ssoService.update(user, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void updateStatus(RoutingContext ctx) {
		String userId = ctx.pathParam("user_id");
		String status = ctx.request().getParam("status");
		ssoService.updateStatus(userId, status, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void delete(RoutingContext ctx) {
		String userId = ctx.pathParam("user_id");
		ssoService.delete(userId, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Bad request")));
			}
		});
	}

}
