package net.miarma.api.microservices.mpaste.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.LogAccumulator;
import net.miarma.api.util.DeploymentUtil;

public class MPasteMainVerticle extends AbstractVerticle {
	
	private ConfigManager configManager;
	
	@Override
	public void start(Promise<Void> startPromise) {
		try {
            this.configManager = ConfigManager.getInstance(); 
            deployVerticles();
            startPromise.complete();
        } catch (Exception e) {
            Constants.LOGGER.error(DeploymentUtil.failMessage(MPasteMainVerticle.class, e));
            startPromise.fail(e);
        }
	}
	
	private void deployVerticles() {
		vertx.deployVerticle(new MPasteDataVerticle(), result -> {
			if (result.succeeded()) {
				String message = String.join("\n\r  ",
						DeploymentUtil.successMessage(MPasteDataVerticle.class),
						DeploymentUtil.apiUrlMessage(
								configManager.getHost(),
								configManager.getIntProperty("mpaste.data.port")
						)
				);
				LogAccumulator.add(message);
			} else {
				LogAccumulator.add(DeploymentUtil.failMessage(MPasteDataVerticle.class, result.cause()));
			}
		});
		
		vertx.deployVerticle(new MPasteLogicVerticle(), result -> {
			if (result.succeeded()) {
				String message = String.join("\n\r  ",
						DeploymentUtil.successMessage(MPasteLogicVerticle.class),
						DeploymentUtil.apiUrlMessage(
								configManager.getHost(),
								configManager.getIntProperty("mpaste.logic.port")
						)
				);
				LogAccumulator.add(message);
			} else {
				LogAccumulator.add(DeploymentUtil.failMessage(MPasteLogicVerticle.class, result.cause()));
			}
		});
	}

}
