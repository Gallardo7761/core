 package net.miarma.api.huertos.routing;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.common.security.SusPather;
import net.miarma.api.huertos.handlers.BalanceLogicHandler;
import net.miarma.api.huertos.handlers.IncomeLogicHandler;
import net.miarma.api.huertos.handlers.MemberLogicHandler;
import net.miarma.api.huertos.handlers.RequestLogicHandler;
import net.miarma.api.huertos.services.MemberService;

public class HuertosLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		MemberLogicHandler hMemberLogic = new MemberLogicHandler(vertx);
		MemberService memberService = new MemberService(pool);
		IncomeLogicHandler hIncomeLogic = new IncomeLogicHandler(vertx);
		BalanceLogicHandler hBalanceLogic = new BalanceLogicHandler(vertx);
		RequestLogicHandler hRequestLogic = new RequestLogicHandler(vertx);
		
		router.route().handler(BodyHandler.create());
		// teapot :P
		router.route().handler(ctx -> {
			String path = ctx.request().path();
			ApiResponse<JsonObject> response = new ApiResponse<JsonObject>(ApiStatus.IM_A_TEAPOT, "I'm a teapot", null);
			JsonObject jsonResponse = new JsonObject().put("status", response.getStatus()).put("message",
					response.getMessage());
			if (SusPather.isSusPath(path)) {
				ctx.response().setStatusCode(response.getStatus()).putHeader("Content-Type", "application/json")
						.end(jsonResponse.encode());
			} else {
				ctx.next();
			}
		});
		
		router.post(HuertosEndpoints.LOGIN).handler(hMemberLogic::login);
		router.get(HuertosEndpoints.MEMBER_BY_NUMBER).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getByMemberNumber);
		router.get(HuertosEndpoints.MEMBER_BY_PLOT).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getByPlotNumber);
		router.get(HuertosEndpoints.MEMBER_BY_DNI).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getByDni);
		router.get(HuertosEndpoints.MEMBER_PAYMENTS).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getUserPayments);
		router.get(HuertosEndpoints.MEMBER_HAS_PAID).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::hasPaid);
		router.get(HuertosEndpoints.MEMBER_WAITLIST).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::getWaitlist);
		router.get(HuertosEndpoints.MEMBER_LIMITED_WAITLIST).handler(hMemberLogic::getLimitedWaitlist);
		router.get(HuertosEndpoints.LAST_MEMBER_NUMBER).handler(hMemberLogic::getLastMemberNumber);
		router.get(HuertosEndpoints.BALANCE_WITH_TOTALS).handler(AuthGuard.huertosAdmin(memberService)).handler(hBalanceLogic::getBalanceWithTotals);
		router.get(HuertosEndpoints.REQUESTS_WITH_PRE_USERS).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestLogic::getRequestsWithPreUsers);
		router.get(HuertosEndpoints.REQUEST_WITH_PRE_USER).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestLogic::getRequestWithPreUser);
		router.get(HuertosEndpoints.MEMBER_PROFILE).handler(hMemberLogic::getProfile);
		router.get(HuertosEndpoints.REQUEST_COUNT).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestLogic::getRequestCount);
		router.get(HuertosEndpoints.MY_INCOMES).handler(AuthGuard.check()).handler(hIncomeLogic::getMyIncomes);
		router.get(HuertosEndpoints.MY_REQUESTS).handler(AuthGuard.check()).handler(hRequestLogic::getMyRequests);
		router.put(HuertosEndpoints.ACCEPT_REQUEST).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestLogic::acceptRequest);
		router.put(HuertosEndpoints.REJECT_REQUEST).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestLogic::rejectRequest);
		router.put(HuertosEndpoints.CHANGE_MEMBER_STATUS).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::changeMemberStatus);
		router.put(HuertosEndpoints.CHANGE_MEMBER_TYPE).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberLogic::changeMemberType);
		router.get(HuertosEndpoints.MEMBER_HAS_COLLABORATOR).handler(AuthGuard.check()).handler(hMemberLogic::hasCollaborator);
		router.get(HuertosEndpoints.MEMBER_HAS_COLLABORATOR_REQUEST).handler(AuthGuard.check()).handler(hMemberLogic::hasCollaboratorRequest);
		router.get(HuertosEndpoints.MEMBER_HAS_GREENHOUSE).handler(AuthGuard.check()).handler(hMemberLogic::hasGreenHouse);
		router.get(HuertosEndpoints.MEMBER_HAS_GREENHOUSE_REQUEST).handler(AuthGuard.check()).handler(hMemberLogic::hasGreenHouseRequest);
		router.post(HuertosEndpoints.PRE_USER_VALIDATE).handler(hMemberLogic::validatePreUser);
	}
}
