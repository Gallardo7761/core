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
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.huertos.routing.HuertosDataRouter;
import net.miarma.api.huertos.services.IncomeService;
import net.miarma.api.huertos.services.MemberService;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.NameCensorer;
import net.miarma.api.util.RouterUtil;

public class HuertosDataVerticle extends AbstractVerticle {

    private ConfigManager configManager;
    private MemberService memberService;
    private IncomeService incomeService;

    @Override
    public void start(Promise<Void> startPromise) {
        configManager = ConfigManager.getInstance();
        Pool pool = DatabaseProvider.createPool(vertx, configManager);

        memberService = new MemberService(pool);
        incomeService = new IncomeService(pool);

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

                default -> EventBusUtil.fail(message).handle(new IllegalArgumentException("Unknown action: " + action));
            }
        });
    }
}
