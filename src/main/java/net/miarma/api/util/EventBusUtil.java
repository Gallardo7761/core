package net.miarma.api.util;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.http.ApiStatus;

public class EventBusUtil {
	public static <T> Handler<Throwable> fail(Message<T> msg) {
	    return err -> {
	        ApiStatus status = ApiStatus.fromException(err);
	        msg.fail(status.getCode(), err.getMessage());
	    };
	}
	
	public static <T> Handler<Throwable> fail(Throwable err) {
	    return _ -> {
	        ApiStatus status = ApiStatus.fromException(err);
	        throw new RuntimeException(status.getDefaultMessage(), err);
	    };
	}
	
	public static void handleReplyError(RoutingContext ctx, Throwable err) {
        if (err instanceof ReplyException replyEx) {
            int code = replyEx.failureCode();
            String message = replyEx.getMessage();

            ApiStatus status = ApiStatus.fromCode(code);
            if (status == null) status = ApiStatus.INTERNAL_SERVER_ERROR;

            JsonUtil.sendJson(ctx, status, null, message);
        } else {
            JsonUtil.sendJson(ctx, ApiStatus.INTERNAL_SERVER_ERROR, null, "Internal server error");
        }
    }
	
	public static void handleReplyError(RoutingContext ctx, Throwable err, String fallbackMsg) {
        if (err instanceof ReplyException replyEx) {
            int code = replyEx.failureCode();
            String message = replyEx.getMessage();

            ApiStatus status = ApiStatus.fromCode(code);
            if (status == null) status = ApiStatus.INTERNAL_SERVER_ERROR;

            JsonUtil.sendJson(ctx, status, null, message != null ? message : fallbackMsg);
        } else {
            JsonUtil.sendJson(ctx, ApiStatus.INTERNAL_SERVER_ERROR, null, fallbackMsg);
        }
    }

}
