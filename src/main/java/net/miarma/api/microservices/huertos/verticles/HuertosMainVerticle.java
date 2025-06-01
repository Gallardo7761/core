package net.miarma.api.microservices.huertos.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.util.DeploymentUtil;

public class HuertosMainVerticle extends AbstractVerticle {
	
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
		vertx.deployVerticle(new HuertosDataVerticle(), result -> {
			if (result.succeeded()) {
				Constants.LOGGER.info(DeploymentUtil.successMessage(HuertosDataVerticle.class));
				Constants.LOGGER.info(DeploymentUtil.apiUrlMessage(
						configManager.getHost(),
						configManager.getIntProperty("huertos.data.port")
				));
			} else {
				Constants.LOGGER.error(DeploymentUtil.failMessage(HuertosDataVerticle.class, result.cause()));
			}
		});
		
		vertx.deployVerticle(new HuertosLogicVerticle(), result -> {
			if (result.succeeded()) {
				Constants.LOGGER.info(DeploymentUtil.successMessage(HuertosLogicVerticle.class));
				Constants.LOGGER.info(DeploymentUtil.apiUrlMessage(
						configManager.getHost(),
						configManager.getIntProperty("huertos.logic.port")
				));
			} else {
				Constants.LOGGER.error(DeploymentUtil.failMessage(HuertosLogicVerticle.class, result.cause()));
			}
		});
	}

}
