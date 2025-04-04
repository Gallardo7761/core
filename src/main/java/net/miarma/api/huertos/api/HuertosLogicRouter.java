package net.miarma.api.huertos.api;

import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.huertos.handlers.MemberLogicHandler;

public class HuertosLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		MemberLogicHandler hMemberLogic = new MemberLogicHandler(vertx);
		
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
		
		router.route().handler(BodyHandler.create());
		
		router.post(HuertosEndpoints.LOGIN).handler(hMemberLogic::login);
		router.get(HuertosEndpoints.USER_BY_MEMBER_NUMBER).handler(AuthGuard.admin()).handler(hMemberLogic::getByMemberNumber);
		router.get(HuertosEndpoints.USER_BY_PLOT_NUMBER).handler(AuthGuard.admin()).handler(hMemberLogic::getByPlotNumber);
		router.get(HuertosEndpoints.USER_BY_DNI).handler(AuthGuard.admin()).handler(hMemberLogic::getByDni);
		router.get(HuertosEndpoints.USER_PAYMENTS).handler(AuthGuard.admin()).handler(hMemberLogic::getUserPayments);
		router.get(HuertosEndpoints.USER_HAS_PAID).handler(AuthGuard.admin()).handler(hMemberLogic::hasPaid);
		router.get(HuertosEndpoints.USER_WAITLIST).handler(hMemberLogic::getWaitlist);
		router.get(HuertosEndpoints.USER_LAST_MEMBER_NUMBER).handler(AuthGuard.admin()).handler(hMemberLogic::getLastMemberNumber);
	}
}
