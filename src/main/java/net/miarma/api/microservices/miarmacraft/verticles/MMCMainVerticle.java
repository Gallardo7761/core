package net.miarma.api.microservices.miarmacraft.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.LogAccumulator;
import net.miarma.api.microservices.huertos.verticles.HuertosLogicVerticle;
import net.miarma.api.microservices.huertos.verticles.HuertosMainVerticle;
import net.miarma.api.util.DeploymentUtil;

public class MMCMainVerticle extends AbstractVerticle {
	private ConfigManager configManager;
	
	@Override
	public void start(Promise<Void> startPromise) {
		try {
			this.configManager = ConfigManager.getInstance(); 
			deployVerticles();
			startPromise.complete();
		} catch (Exception e) {
            Constants.LOGGER.error(DeploymentUtil.failMessage(HuertosMainVerticle.class, e));
			startPromise.fail(e);
		}
	}
	
	private void deployVerticles() {
		vertx.deployVerticle(new MMCDataVerticle(), result -> {
			if (result.succeeded()) {
				String message = String.join("\n\r  ",
						DeploymentUtil.successMessage(MMCDataVerticle.class),
						DeploymentUtil.apiUrlMessage(
								configManager.getHost(),
								configManager.getIntProperty("mmc.data.port")
						)
				);
				LogAccumulator.add(message);
			} else {
				LogAccumulator.add(DeploymentUtil.failMessage(HuertosLogicVerticle.class, result.cause()));
			}
		});
		
		vertx.deployVerticle(new MMCLogicVerticle(), result -> {
			if (result.succeeded()) {
				String message = String.join("\n\r  ",
						DeploymentUtil.successMessage(MMCLogicVerticle.class),
						DeploymentUtil.apiUrlMessage(
								configManager.getHost(),
								configManager.getIntProperty("mmc.logic.port")
						)
				);
				LogAccumulator.add(message);
			} else {
				LogAccumulator.add(DeploymentUtil.failMessage(HuertosLogicVerticle.class, result.cause()));
			}
		});
	}

}
