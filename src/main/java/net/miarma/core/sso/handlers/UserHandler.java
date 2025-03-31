package net.miarma.core.sso.handlers;

import java.util.stream.Collectors;

import com.google.gson.Gson;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
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
				 String result = ar.result().stream()
						 .map(UserEntity::encode)
						 .collect(Collectors.joining(", ", "[", "]"));
				 ctx.response().putHeader("Content-Type", "application/json")
					.end(result);
			 } else {
				 ctx.response().setStatusCode(500).end(ar.cause().getMessage());
			 }
		 });
	 }
	 
	 public void getById(RoutingContext ctx) {
		 ssoService.getById(ctx.pathParam("user_id"), ar -> {
			 if (ar.succeeded()) {
				 ctx.response().putHeader("Content-Type", "application/json")
					.end(ar.result().encode().toString());
			 } else {
				 ctx.response().setStatusCode(404).end(ar.cause().getMessage());
			 }
		 });
	 }
	 
	 public void getByEmail(RoutingContext ctx) {
		 ssoService.getByEmail(ctx.pathParam("email"), ar -> {
			 if (ar.succeeded()) {
				 ctx.response().putHeader("Content-Type", "application/json")
					.end(ar.result().encode());
			 } else {
				 ctx.response().setStatusCode(404).end(ar.cause().getMessage());
			 }
		 });
	 }
	 
	 public void getByUserName(RoutingContext ctx) {
		 ssoService.getByUserName(ctx.pathParam("user_name"), ar -> {
			 if (ar.succeeded()) {
				 ctx.response().putHeader("Content-Type", "application/json")
					.end(ar.result().encode());
			 } else {
				 ctx.response().setStatusCode(404).end(ar.cause().getMessage());
			 }
		 });
	 }
	 
	 public void getStatus(RoutingContext ctx) {
		    String userId = ctx.pathParam("user_id");
		    
		    ssoService.getById(userId, ar -> {
		        if (ar.succeeded()) {
		            UserEntity user = ar.result();

		            JsonObject response = new JsonObject()
		                .put("user_id", user.getUser_id())
		                .put("status", user.getGlobal_status());

		            ctx.response()
		                .putHeader("Content-Type", "application/json")
		                .end(response.encode());
		        } else {
		            ctx.response()
		                .setStatusCode(404)
		                .end(ar.cause().getMessage());
		        }
		    });
		}

	 
	 public void getRole(RoutingContext ctx) {
	     String userId = ctx.pathParam("user_id");

	     ssoService.getById(userId, ar -> {
	         if (ar.succeeded()) {
	             UserEntity user = ar.result();

	             JsonObject response = new JsonObject()
	                 .put("user_id", user.getUser_id())
	                 .put("roles", user.getRole());

	             ctx.response()
	                 .putHeader("Content-Type", "application/json")
	                 .end(response.encode());
	         } else {
	             ctx.response()
	                 .setStatusCode(404)
	                 .end(ar.cause().getMessage());
	         }
	     });
	 }

	 public void getInfo(RoutingContext ctx) {
	     String userId = ctx.pathParam("user_id");

	     ssoService.getById(userId, ar -> {
	         if (ar.succeeded()) {
	             UserEntity user = ar.result();

	             JsonObject response = new JsonObject()
	                 .put("user_id", user.getUser_id())
	                 .put("user_name", user.getUser_name())
	                 .put("email", user.getEmail())
	                 .put("avatar", user.getAvatar())
	                 .put("status", user.getGlobal_status());

	             ctx.response()
	                 .putHeader("Content-Type", "application/json")
	                 .end(response.encode());
	         } else {
	             ctx.response()
	                 .setStatusCode(404)
	                 .end(ar.cause().getMessage());
	         }
	     });
	 }

	 public void exists(RoutingContext ctx) {
	     String userId = ctx.pathParam("user_id");

	     ssoService.getById(userId, ar -> {
	         JsonObject response = new JsonObject()
	             .put("user_id", Integer.parseInt(userId))
	             .put("exists", ar.succeeded() && ar.result() != null);

	         ctx.response()
	             .putHeader("Content-Type", "application/json")
	             .end(response.encode());
	     });
	 }

	 public void getAvatar(RoutingContext ctx) {
	     String userId = ctx.pathParam("user_id");

	     ssoService.getById(userId, ar -> {
	         if (ar.succeeded()) {
	             UserEntity user = ar.result();

	             JsonObject response = new JsonObject()
	                 .put("user_id", user.getUser_id())
	                 .put("avatar", user.getAvatar());

	             ctx.response()
	                 .putHeader("Content-Type", "application/json")
	                 .end(response.encode());
	         } else {
	             ctx.response()
	                 .setStatusCode(404)
	                 .end(ar.cause().getMessage());
	         }
	     });
	 }
	 
	 public void create(RoutingContext ctx) {
		 UserEntity user = gson.fromJson(ctx.body().asString(), UserEntity.class);
		 ssoService.create(user, ar -> {
			 if (ar.succeeded()) {
				 ctx.response().setStatusCode(201).end(ar.result().encode());
			 } else {
				 ctx.response().setStatusCode(400).end(ar.cause().getMessage());
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
				 ctx.response().setStatusCode(400).end(ar.cause().getMessage());
			 }
		 });
	 }
	 
	 public void update(RoutingContext ctx) {
		 UserEntity user = gson.fromJson(ctx.body().asString(), UserEntity.class);
		 ssoService.update(user, ar -> {
			 if (ar.succeeded()) {
				 ctx.response().setStatusCode(204).end();
			 } else {
				 ctx.response().setStatusCode(400).end(ar.cause().getMessage());
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
				 ctx.response().setStatusCode(400).end(ar.cause().getMessage());
			 }
		 });
	 }
	 
	 public void delete(RoutingContext ctx) {
		 String userId = ctx.pathParam("user_id");
		 ssoService.delete(userId, ar -> {
			 if (ar.succeeded()) {
				 ctx.response().setStatusCode(204).end();
			 } else {
				 ctx.response().setStatusCode(400).end(ar.cause().getMessage());
			 }
		 });
	 }
	 	 
}
