package net.miarma.api.core.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.core.handlers.FileLogicHandler;
import net.miarma.api.core.handlers.UserLogicHandler;

public class CoreLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		UserLogicHandler hUserLogic = new UserLogicHandler(vertx);
		FileLogicHandler hFileLogic = new FileLogicHandler(vertx);
		
		Set<HttpMethod> allowedMethods = new HashSet<>(
	    		Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.OPTIONS)); // Por ejemplo
		Set<String> allowedHeaders = new HashSet<>(Arrays.asList("Content-Type", "Authorization"));

        router.route().handler(CorsHandler.create()
                .allowCredentials(true)
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods));
        
        router.options()
        	.pathRegex(".*")
			.handler(routingContext -> routingContext.response()
					.putHeader("Content-Type", "application/json")
					.setStatusCode(200).end());
        
		router.route().handler(BodyHandler.create());
		
		router.post(CoreEndpoints.LOGIN).handler(hUserLogic::login);
		router.get(CoreEndpoints.USER_INFO).handler(AuthGuard.check()).handler(hUserLogic::getInfo);
        router.post(CoreEndpoints.REGISTER).handler(hUserLogic::register);
        router.post(CoreEndpoints.CHANGE_PASSWORD).handler(AuthGuard.check()).handler(hUserLogic::changePassword);
        router.get(CoreEndpoints.VALIDATE_TOKEN).handler(hUserLogic::validateToken);
		
        router.get(CoreEndpoints.USER_EXISTS).handler(AuthGuard.check()).handler(hUserLogic::exists);
		router.get(CoreEndpoints.USER_STATUS).handler(AuthGuard.check()).handler(hUserLogic::getStatus);
		router.put(CoreEndpoints.USER_STATUS).handler(AuthGuard.admin()).handler(hUserLogic::updateStatus);
		router.get(CoreEndpoints.USER_ROLE).handler(AuthGuard.check()).handler(hUserLogic::getRole);
		router.put(CoreEndpoints.USER_ROLE).handler(AuthGuard.admin()).handler(hUserLogic::updateRole);
		router.get(CoreEndpoints.USER_AVATAR).handler(AuthGuard.check()).handler(hUserLogic::getAvatar);
		
		router.get(CoreEndpoints.FILE_DOWNLOAD).handler(AuthGuard.check()).handler(hFileLogic::downloadFile);
		router.get(CoreEndpoints.USER_FILES).handler(AuthGuard.check()).handler(hFileLogic::getUserFiles);
	}
}
