package net.miarma.api.util;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import net.miarma.api.common.http.ApiStatus;

public class EventBusUtil {
	public static <T> Handler<Throwable> fail(Message<T> msg) {
	    return err -> {
	        ApiStatus status = ApiStatus.fromException(err);
	        msg.fail(status.getCode(), err.getMessage());
	    };
	}
	
	public static <T> Handler<Throwable> fail(Throwable err) {
	    return e -> {
	        ApiStatus status = ApiStatus.fromException(err);
	        throw new RuntimeException(status.getDefaultMessage(), err);
	    };
	}
	
	

}
