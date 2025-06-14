package net.miarma.api.common.middlewares;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.HuertosUserRole;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.microservices.huertos.entities.MemberEntity;
import net.miarma.api.microservices.huertos.services.MemberService;
import net.miarma.api.microservices.huertosdecine.entities.ViewerEntity;
import net.miarma.api.microservices.huertosdecine.services.ViewerService;
import net.miarma.api.util.JsonUtil;

/**
 * Middleware para verificar la autenticación y autorización de los usuarios.
 * Este middleware comprueba si el usuario está autenticado mediante un token JWT
 * y si tiene los permisos necesarios para acceder a ciertos recursos.
 *
 * @author José Manuel Amador Gallardo
 */
public class AuthGuard {

	/**
	 * Middleware para verificar si el usuario está autenticado.
	 * @return Handler<RoutingContext> que verifica el token JWT en la cabecera de autorización.
	 */
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

	/**
	 * Middleware para verificar si el usuario es un administrador global.
	 * @return Handler<RoutingContext> que verifica el token JWT y los permisos de administrador.
	 */
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

	/**
	 * Middleware para verificar si el usuario es un administrador de huertos.
	 * @param memberService Servicio para obtener información del miembro.
	 * @return Handler<RoutingContext> que verifica el token JWT y los permisos de administrador de huertos.
	 */
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

	/**
	 * Middleware para verificar si el usuario es un administrador de cine.
	 * @param viewerService Servicio para obtener información del espectador.
	 * @return Handler<RoutingContext> que verifica el token JWT y los permisos de administrador de cine.
	 */
	public static Handler<RoutingContext> cineAdmin (ViewerService viewerService) {
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

			viewerService.getById(userId).onComplete(ar -> {
				if (ar.succeeded() && ar.result() != null) {
					ViewerEntity viewer = ar.result();
					if (viewer.getRole() == Constants.CineUserRole.ADMIN) {
						ctx.put("viewer", viewer);
						ctx.next();
					} else {
						JsonUtil.sendJson(ctx, ApiStatus.FORBIDDEN, "You are not a cine administrator");
					}
				} else {
					JsonUtil.sendJson(ctx, ApiStatus.FORBIDDEN, "You are not registered in the system");
				}
			});
		};
	}


}
