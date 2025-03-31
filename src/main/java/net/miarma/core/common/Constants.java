package net.miarma.core.common;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class Constants {
	public static final String APP_NAME = "MiarmaCoreAPI";
	public static final String API_PREFIX = "/api/v1";
    public static Logger LOGGER = LoggerFactory.getLogger(Constants.APP_NAME);
    
	public static final Integer SSO_LOGIC_PORT = 8080;
	public static final Integer SSO_DATA_PORT = 8081;
	public static final Integer MMC_LOGIC_PORT = 8100;
	public static final Integer MMC_DATA_PORT = 8101;
	public static final Integer HUERTOS_LOGIC_PORT = 8120;
	public static final Integer HUERTOS_DATA_PORT = 8121;
    
	private Constants() {
        throw new AssertionError("Utility class cannot be instantiated.");
    }
}
