package net.miarma.api.huertos.verticles;

import java.util.stream.Collectors;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.huertos.routing.HuertosDataRouter;
import net.miarma.api.huertos.services.BalanceService;
import net.miarma.api.huertos.services.IncomeService;
import net.miarma.api.huertos.services.MemberService;
import net.miarma.api.huertos.services.RequestService;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.NameCensorer;
import net.miarma.api.util.RouterUtil;

public class HuertosDataVerticle extends AbstractVerticle {

    private ConfigManager configManager;
    private MemberService memberService;
    private IncomeService incomeService;
    private BalanceService balanceService;
    private RequestService requestService;

    @Override
    public void start(Promise<Void> startPromise) {
        configManager = ConfigManager.getInstance();
        Pool pool = DatabaseProvider.createPool(vertx, configManager);

        memberService = new MemberService(pool);
        incomeService = new IncomeService(pool);
        balanceService = new BalanceService(pool);
        requestService = new RequestService(pool);
        
        Router router = Router.router(vertx);
        RouterUtil.attachLogger(router);
        HuertosDataRouter.mount(router, vertx, pool);
        registerLogicVerticleConsumer();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(configManager.getIntProperty("huertos.data.port"), res -> {
                if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
            });
    }

    private void registerLogicVerticleConsumer() {
        vertx.eventBus().consumer(Constants.HUERTOS_EVENT_BUS, message -> {
            JsonObject body = (JsonObject) message.body();
            String action = body.getString("action");

            switch (action) {
                case "login" -> {
                    String email = body.getString("email", null);
                    String userName = body.getString("userName", null);
                    String password = body.getString("password");
                    boolean keepLoggedIn = body.getBoolean("keepLoggedIn", false);

                    memberService.login(email != null ? email : userName, password, keepLoggedIn)
                        .onSuccess(message::reply)
                        .onFailure(EventBusUtil.fail(message));
                }

                case "getByMemberNumber" ->
	                memberService.getByMemberNumber(body.getInteger("memberNumber"))
	                    .onSuccess(member -> message.reply(new JsonObject(Constants.GSON.toJson(member))))
	                    .onFailure(EventBusUtil.fail(message));


                case "getByPlotNumber" ->
	                memberService.getByPlotNumber(body.getInteger("plotNumber"))
	                    .onSuccess(member -> message.reply(new JsonObject(Constants.GSON.toJson(member))))
	                    .onFailure(EventBusUtil.fail(message));


                case "getByDNI" ->
	                memberService.getByDni(body.getString("dni"))
	                    .onSuccess(member -> message.reply(new JsonObject(Constants.GSON.toJson(member))))
	                    .onFailure(EventBusUtil.fail(message));

                case "getUserPayments" -> incomeService.getUserPayments(body.getInteger("memberNumber"))
                    .onSuccess(payments -> {
                    	String paymentsJson = payments.stream()
							.map(payment -> Constants.GSON.toJson(payment))
							.collect(Collectors.joining(",", "[", "]"));
						message.reply(new JsonArray(paymentsJson));
                    })
                    .onFailure(EventBusUtil.fail(message));

                case "hasPaid" -> incomeService.hasPaid(body.getInteger("memberNumber"))
                    .onSuccess(result -> message.reply(new JsonObject().put("hasPaid", result)))
                    .onFailure(EventBusUtil.fail(message));

                case "getWaitlist" ->
	                memberService.getWaitlist()
	                    .onSuccess(list -> {
	                    	String listJson = list.stream()
	                    			.map(member -> Constants.GSON.toJson(member))
	                    			.collect(Collectors.joining(",", "[", "]"));
	                        message.reply(new JsonArray(listJson));
	                    })
	                    .onFailure(EventBusUtil.fail(message));
	                
				case "getLimitedWaitlist" ->
					memberService.getWaitlist()
						.onSuccess(list -> {
							String listJson = list.stream()
									.map(member -> {
										JsonObject json = new JsonObject(Constants.GSON.toJson(member));
										json.put("display_name", NameCensorer.censor(json.getString("display_name")));
										json.remove("user_id");
										json.remove("member_number");
										json.remove("plot_number");
										json.remove("dni");
										json.remove("phone");
										json.remove("email");
										json.remove("user_name");
										json.remove("notes");
										json.remove("type");
										json.remove("status");
										json.remove("role");
										json.remove("global_status");
										json.remove("global_role");
										return json;
									})
									.map(member -> Constants.GSON.toJson(member))
									.collect(Collectors.joining(",", "[", "]"));
							message.reply(new JsonArray(listJson));
						})
						.onFailure(EventBusUtil.fail(message));

                case "getLastMemberNumber" -> memberService.getLastMemberNumber()
                    .onSuccess(last -> message.reply(new JsonObject().put("lastMemberNumber", last)))
                    .onFailure(EventBusUtil.fail(message));
                
                case "getIncomesWithNames" -> incomeService.getIncomesWithNames()
                    .onSuccess(incomes -> {
                    	String incomesJson = incomes.stream()
								.map(income -> Constants.GSON.toJson(income))
								.collect(Collectors.joining(",", "[", "]"));
						message.reply(new JsonArray(incomesJson));
                    })
                    .onFailure(EventBusUtil.fail(message));
                
                case "getBalanceWithTotals" -> balanceService.getBalanceWithTotals()
                    .onSuccess(balance -> {
                    	String balanceJson = Constants.GSON.toJson(balance);
                    	message.reply(new JsonObject(balanceJson));	
                    })
                    .onFailure(EventBusUtil.fail(message));

                case "getRequestsWithPreUsers" -> requestService.getRequestsWithPreUsers()
					.onSuccess(requests -> {
						String requestsJson = requests.stream()
								.map(request -> Constants.GSON.toJson(request))
								.collect(Collectors.joining(",", "[", "]"));
						message.reply(new JsonArray(requestsJson));
					})
					.onFailure(EventBusUtil.fail(message));
                
                case "getRequestWithPreUser" -> requestService.getRequestWithPreUserById(body.getInteger("requestId"))
					.onSuccess(request -> {
						String requestJson = Constants.GSON.toJson(request);
						message.reply(new JsonObject(requestJson));
					})
					.onFailure(EventBusUtil.fail(message));
                
                case "getProfile" -> memberService.getProfile(body.getString("token"))
                	.onSuccess(profile -> {
						String profileJson = Constants.GSON.toJson(profile);
						message.reply(new JsonObject(profileJson));
					})
					.onFailure(EventBusUtil.fail(message));
                
                case "getRequestCount" -> requestService.getRequestCount()
					.onSuccess(count -> message.reply(new JsonObject().put("count", count)))
					.onFailure(EventBusUtil.fail(message));
                
                case "getMyIncomes" -> incomeService.getMyIncomes(body.getString("token"))
					.onSuccess(incomes -> {
						String incomesJson = incomes.stream()
								.map(income -> Constants.GSON.toJson(income))
								.collect(Collectors.joining(",", "[", "]"));
						message.reply(new JsonArray(incomesJson));
						})
					.onFailure(EventBusUtil.fail(message));
                
                case "getMyRequests" -> requestService.getMyRequests(body.getString("token"))
					.onSuccess(requests -> {
						String requestsJson = requests.stream()
								.map(request -> Constants.GSON.toJson(request))
								.collect(Collectors.joining(",", "[", "]"));
						message.reply(new JsonArray(requestsJson));
					})
					.onFailure(EventBusUtil.fail(message));
                
                case "hasCollaborator" -> memberService.hasCollaborator(body.getString("token"))
					.onSuccess(result -> message.reply(new JsonObject().put("hasCollaborator", result)))
					.onFailure(EventBusUtil.fail(message));
                
                case "hasCollaboratorRequest" -> requestService.hasCollaboratorRequest(body.getString("token"))
					.onSuccess(result -> message.reply(new JsonObject().put("hasCollaboratorRequest", result)))
					.onFailure(EventBusUtil.fail(message));
                
                case "hasGreenhouse" -> memberService.hasGreenHouse(body.getString("token"))
					.onSuccess(result -> message.reply(new JsonObject().put("hasGreenhouse", result)))
					.onFailure(EventBusUtil.fail(message));
                
                case "hasGreenhouseRequest" -> requestService.hasGreenHouseRequest(body.getString("token"))
					.onSuccess(result -> message.reply(new JsonObject().put("hasGreenhouseRequest", result)))
					.onFailure(EventBusUtil.fail(message));
                
                case "acceptRequest" -> requestService.acceptRequest(body.getInteger("requestId"))
					.onSuccess(request -> {
						String requestJson = Constants.GSON.toJson(request);
						message.reply(new JsonObject(requestJson));
					})
					.onFailure(EventBusUtil.fail(message));
                
                case "rejectRequest" -> requestService.rejectRequest(body.getInteger("requestId"))
					.onSuccess(request -> {
						String requestJson = Constants.GSON.toJson(request);
						message.reply(new JsonObject(requestJson));	
					})
					.onFailure(EventBusUtil.fail(message));
                
                case "changeMemberStatus" -> {
                	HuertosUserStatus status = HuertosUserStatus.fromInt(body.getInteger("status"));
                	memberService.changeMemberStatus(body.getInteger("memberNumber"), status)
					.onSuccess(member -> {
						String memberJson = Constants.GSON.toJson(member);
						message.reply(new JsonObject(memberJson));
					})
					.onFailure(EventBusUtil.fail(message));
                }
                
                case "changeMemberType" -> {
                	HuertosUserType type = HuertosUserType.fromInt(body.getInteger("type"));
                	memberService.changeMemberType(body.getInteger("memberNumber"), type)
					.onSuccess(member -> {
						String memberJson = Constants.GSON.toJson(member);
						message.reply(new JsonObject(memberJson));
					})
					.onFailure(EventBusUtil.fail(message));
                }
                
                default -> EventBusUtil.fail(message).handle(new IllegalArgumentException("Unknown action: " + action));
            }
        });
    }
}
