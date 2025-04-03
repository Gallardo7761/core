package net.miarma.api.huertos.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.huertos.handlers.MemberLogicHandler;

public class HuertosLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		MemberLogicHandler hMemberLogic = new MemberLogicHandler(vertx);
		
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
