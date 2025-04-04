package net.miarma.api.core.api;

import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.core.handlers.FileDataHandler;
import net.miarma.api.core.handlers.UserDataHandler;

public class CoreDataRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		UserDataHandler hUserData = new UserDataHandler(pool);
		FileDataHandler hFileData = new FileDataHandler(pool);
		
		Set<HttpMethod> allowedMethods = Set.of(HttpMethod.GET, HttpMethod.POST, 
				HttpMethod.PUT, HttpMethod.DELETE,
				HttpMethod.OPTIONS);

		router.route().handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Allow-Origin", "*");
			routingContext.response().putHeader("Access-Control-Allow-Methods", String.join(",", allowedMethods.stream().map(HttpMethod::name).toArray(String[]::new)));
			routingContext.response().putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
			routingContext.next();
		});
		
		router.options()
	    	.pathRegex(".*")
			.handler(routingContext -> routingContext.response()
					.putHeader("Content-Type", "application/json")
					.setStatusCode(200).end());
		
		router.get(CoreEndpoints.USERS).handler(AuthGuard.admin()).handler(hUserData::getAll);
		router.get(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hUserData::getById);
		router.post(CoreEndpoints.USERS).handler(hUserData::create);
		router.put(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hUserData::update);
		router.delete(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hUserData::delete);
		
		router.get(CoreEndpoints.FILES).handler(AuthGuard.check()).handler(hFileData::getAll);
		router.get(CoreEndpoints.FILE).handler(AuthGuard.check()).handler(hFileData::getById);
		router.post(CoreEndpoints.FILE_UPLOAD).handler(AuthGuard.check()).handler(hFileData::create);
		router.put(CoreEndpoints.FILE).handler(AuthGuard.check()).handler(hFileData::update);
		router.delete(CoreEndpoints.FILE).handler(AuthGuard.check()).handler(hFileData::delete);
		
	}
}
