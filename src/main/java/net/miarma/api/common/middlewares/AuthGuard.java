package net.miarma.api.common.middlewares;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.security.JWTManager;

public class AuthGuard {
	public static Handler<RoutingContext> check() {
		return ctx -> {
			String authHeader = ctx.request().getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            String token = authHeader.substring(7);
	            if (JWTManager.getInstance().isValid(token)) {
	                ctx.next();
	                return;
	            }
	        }
			
			ctx.response()
	           .setStatusCode(401)
	           .putHeader("Content-Type", "application/json")
	           .end("{\"error\": \"Unauthorized\"}");
		};
	}
	
	public static Handler<RoutingContext> admin() {
		return ctx -> {
			String authHeader = ctx.request().getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            String token = authHeader.substring(7);
	            if (JWTManager.getInstance().isAdmin(token)) {
	                ctx.next();
	                return;
	            }
	        }
			
			ctx.response()
	           .setStatusCode(401)
	           .putHeader("Content-Type", "application/json")
	           .end("{\"error\": \"Unauthorized\"}");
		};
	}
}
