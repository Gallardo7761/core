package net.miarma.api.common.middlewares;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants.HuertosUserRole;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.huertos.entities.MemberEntity;
import net.miarma.api.huertos.services.MemberService;
import net.miarma.api.util.JsonUtil;

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

			JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, "Invalid or missing token");
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

			JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, "You are not a global administrator");
		};
	}


	public static Handler<RoutingContext> huertosAdmin(MemberService memberService) {
		return ctx -> {
			String authHeader = ctx.request().getHeader("Authorization");
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, "Invalid or missing token");
				return;
			}

			String token = authHeader.substring(7);
			if (!JWTManager.getInstance().isValid(token)) {
				JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, "Invalid or missing token");
				return;
			}

			int userId = JWTManager.getInstance().extractUserId(token);

			memberService.getById(userId).onComplete(ar -> {
				if (ar.succeeded() && ar.result() != null) {
					MemberEntity member = ar.result();

					if (member.getRole() == HuertosUserRole.ADMIN || member.getRole() == HuertosUserRole.DEV) {
						ctx.put("member", member);
						ctx.next();
					} else {
						JsonUtil.sendJson(ctx, ApiStatus.FORBIDDEN, "You are not a huertos administrator");
					}
				} else {
					JsonUtil.sendJson(ctx, ApiStatus.FORBIDDEN, "You are not registered in the system");
				}
			});
		};
	}


}
