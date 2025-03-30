package net.miarma.core.sso.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.deployVerticle(new AuthVerticle(), res -> {
			if (res.succeeded()) {
				System.out.println("AuthVerticle desplegado");
			} else {
				System.err.println(res.cause());
			}
		});
		startPromise.complete();
	}

	
	@Override
	public void stop(Promise<Void> stopPromise) {
		vertx.deploymentIDs().forEach(id -> vertx.undeploy(id));
		stopPromise.complete();
		
	}
}
