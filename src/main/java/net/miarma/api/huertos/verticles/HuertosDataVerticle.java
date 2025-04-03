package net.miarma.api.huertos.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.huertos.api.HuertosDataRouter;
import net.miarma.api.huertos.services.IncomeService;
import net.miarma.api.huertos.services.MemberService;
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
					String emailOrUserName = body.getString("emailOrUserName");
					String password = body.getString("password");
					boolean keepLoggedIn = body.getBoolean("keepLoggedIn", false);

					memberService.login(emailOrUserName, password, keepLoggedIn)
						.onSuccess(res -> {
							JsonObject json = new JsonObject(Constants.GSON.toJson(res));
							message.reply(json);
						})
						.onFailure(err -> message.fail(401, err.getMessage()));
				}

				case "getByMemberNumber" -> {
					Integer memberNumber = body.getInteger("memberNumber");

					memberService.getByMemberNumber(memberNumber)
						.onSuccess(res -> {
							JsonObject json = new JsonObject(Constants.GSON.toJson(res));
							message.reply(json);
						})
						.onFailure(err -> message.fail(404, err.getMessage()));
				}

				case "getByPlotNumber" -> {
					Integer plotNumber = body.getInteger("plotNumber");

					memberService.getByPlotNumber(plotNumber)
						.onSuccess(res -> {
							JsonObject json = new JsonObject(Constants.GSON.toJson(res));
							message.reply(json);
						})
						.onFailure(err -> message.fail(404, err.getMessage()));
				}

				case "getByDNI" -> {
					String dni = body.getString("dni");

					memberService.getByDni(dni)
						.onSuccess(res -> {
							JsonObject json = new JsonObject(Constants.GSON.toJson(res));
							message.reply(json);
						})
						.onFailure(err -> message.fail(404, err.getMessage()));
				}

				case "getUserPayments" -> {
					Integer memberNumber = body.getInteger("memberNumber");

					incomeService.getUserPayments(memberNumber)
						.onSuccess(res -> {
							// Si es una lista, conviértelo en array
							message.reply(new JsonObject().put("payments", Constants.GSON.toJson(res)));
						})
						.onFailure(err -> message.fail(404, err.getMessage()));
				}

				case "hasPaid" -> {
					Integer memberNumber = body.getInteger("memberNumber");

					incomeService.hasPaid(memberNumber)
						.onSuccess(res -> {
							message.reply(new JsonObject().put("hasPaid", res));
						})
						.onFailure(err -> message.fail(404, err.getMessage()));
				}
				
				case "getWaitlist" -> {
					memberService.getWaitlist()
						.onSuccess(res -> {
							message.reply(new JsonObject().put("waitlist", Constants.GSON.toJson(res)));
						})
						.onFailure(err -> message.fail(404, err.getMessage()));
				}
				
				case "getLastMemberNumber" -> {
					memberService.getLastMemberNumber()
						.onSuccess(res -> {
							message.reply(new JsonObject().put("lastMemberNumber", res));
						})
						.onFailure(err -> message.fail(404, err.getMessage()));
				}

				default -> message.fail(400, "Unknown action: " + action);
			}
		});
	}


}
