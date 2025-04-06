package net.miarma.api.huertos.routing;

import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.huertos.handlers.MemberLogicHandler;
import net.miarma.api.huertos.services.MemberService;

public class HuertosLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		MemberLogicHandler hMemberLogic = new MemberLogicHandler(vertx);
		MemberService memberService = new MemberService(pool);
		
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
		router.get(HuertosEndpoints.MEMBER_BY_NUMBER).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getByMemberNumber);
		router.get(HuertosEndpoints.MEMBER_BY_PLOT).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getByPlotNumber);
		router.get(HuertosEndpoints.MEMBER_BY_DNI).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getByDni);
		router.get(HuertosEndpoints.MEMBER_PAYMENTS).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getUserPayments);
		router.get(HuertosEndpoints.MEMBER_HAS_PAID).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::hasPaid);
		router.get(HuertosEndpoints.MEMBER_WAITLIST).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getWaitlist);
		router.get(HuertosEndpoints.MEMBER_LIMITED_WAITLIST).handler(hMemberLogic::getLimitedWaitlist);
		router.get(HuertosEndpoints.LAST_MEMBER_NUMBER).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getLastMemberNumber);
	}
}
