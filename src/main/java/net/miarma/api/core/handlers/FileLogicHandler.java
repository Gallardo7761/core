package net.miarma.api.core.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.common.security.JWTManager;

public class FileLogicHandler {
	
	private final Vertx vertx;
    
    public FileLogicHandler(Vertx vertx) {
        this.vertx = vertx;
    }
    
    public void getUserFiles(RoutingContext ctx) {
		String authHeader = ctx.request().getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.response().setStatusCode(401).end(Constants.GSON.toJson(SingleJsonResponse.of("Unauthorized")));
            return;
        }

        String token = authHeader.substring(7);
        int userId = JWTManager.getInstance().getUserId(token);
        
        if (userId <= 0) {
            ctx.response().setStatusCode(401).end(Constants.GSON.toJson(SingleJsonResponse.of("Invalid token")));
            return;
        }
        
        JsonObject request = new JsonObject()
                .put("action", "getUserFiles")
                .put("userId", userId);
        
        vertx.eventBus().request(Constants.CORE_EVENT_BUS, request, ar -> {
        	if (ar.succeeded()) {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body()).encode());
            } else {
                ctx.response()
                        .setStatusCode(404)
                        .end(Constants.GSON.toJson(SingleJsonResponse.of("The user has no files")));
            }
        });
    }
    
    public void uploadFile(RoutingContext ctx) {
		String authHeader = ctx.request().getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			ctx.response().setStatusCode(401).end(Constants.GSON.toJson(SingleJsonResponse.of("Unauthorized")));
			return;
		}

		String token = authHeader.substring(7);
		int userId = JWTManager.getInstance().getUserId(token);
		
		if (userId <= 0) {
			ctx.response().setStatusCode(401).end(Constants.GSON.toJson(SingleJsonResponse.of("Invalid token")));
			return;
		}
		
		JsonObject request = new JsonObject()
				.put("action", "uploadFile")
				.put("userId", userId)
				.put("file", ctx.body().asJsonObject());
		
		vertx.eventBus().request(Constants.CORE_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				ctx.response()
						.putHeader("Content-Type", "application/json")
						.end(((JsonObject) ar.result().body()).encode());
			} else {
				ctx.response()
						.setStatusCode(404)
						.end(Constants.GSON.toJson(SingleJsonResponse.of("Error uploading file")));
			}
		});
	}
    
    public void downloadFile(RoutingContext ctx) {
		String authHeader = ctx.request().getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			ctx.response().setStatusCode(401).end(Constants.GSON.toJson(SingleJsonResponse.of("Unauthorized")));
			return;
		}
		
		String token = authHeader.substring(7);
		int userId = JWTManager.getInstance().getUserId(token);
		
		if (userId <= 0) {
			ctx.response().setStatusCode(401).end(Constants.GSON.toJson(SingleJsonResponse.of("Invalid token")));
			return;
		}
		
		int fileId = Integer.parseInt(ctx.pathParam("file_id"));
		
		JsonObject request = new JsonObject()
				.put("action", "downloadFile")
				.put("userId", userId)
				.put("fileId", fileId);
		
		vertx.eventBus().request(Constants.CORE_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				ctx.response()
						.putHeader("Content-Type", "application/json")
						.end(((JsonObject) ar.result().body()).encode());
			} else {
				ctx.response()
						.setStatusCode(404)
						.end(Constants.GSON.toJson(SingleJsonResponse.of("Error downloading file")));
			}
		});
    }
    
}
