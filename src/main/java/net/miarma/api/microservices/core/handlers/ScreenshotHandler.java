package net.miarma.api.microservices.core.handlers;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.util.JsonUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ScreenshotHandler {
	
	private WebClient webClient;
	
	public ScreenshotHandler(Vertx vertx) {
		this.webClient = WebClient.create(vertx);
	}
	
	public void getScreenshot(RoutingContext ctx) {
		String url = ctx.request().getParam("url");
		
		if (url == null || url.isEmpty()) {
			ctx.response().setStatusCode(400).end("URL parameter is required");
			return;
		}
		
		String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
	    String microserviceUrl = "http://localhost:7000/screenshot?url=" + encodedUrl;
	    
	    webClient.getAbs(microserviceUrl)
        .send(ar -> {
            if (ar.succeeded()) {
                ctx.response()
                    .putHeader("Content-Type", "image/png")
                    .end(ar.result().body());
            } else {
                JsonUtil.sendJson(ctx, ApiStatus.INTERNAL_SERVER_ERROR, null, "Could not generate the screenshot");
            }
        });
	    
	}
	
}
