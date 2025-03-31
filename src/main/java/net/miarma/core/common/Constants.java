package net.miarma.core.common;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class Constants {
	public static final String APP_NAME = "MiarmaCoreAPI";
	public static final String API_PREFIX = "/api/v1";
	public static final String SSO_PREFIX = "/auth/v1";
    public static final String AUTH_EVENT_BUS = "auth.eventbus";
    public static Logger LOGGER = LoggerFactory.getLogger(Constants.APP_NAME);
    
	public static final Integer SSO_LOGIC_PORT = 8080;
	public static final Integer SSO_DATA_PORT = 8081;
	public static final Integer MMC_LOGIC_PORT = 8100;
	public static final Integer MMC_DATA_PORT = 8101;
	public static final Integer HUERTOS_LOGIC_PORT = 8120;
	public static final Integer HUERTOS_DATA_PORT = 8121;
    
	public enum SSOUserRole {
	    USER(0),
	    ADMIN(1);

	    private final int value;

	    SSOUserRole(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static SSOUserRole fromInt(int i) {
	        for (SSOUserRole role : values()) {
	            if (role.value == i) return role;
	        }
	        throw new IllegalArgumentException("Invalid SSOUserRole value: " + i);
	    }
	}

	
	public enum SSOUserGlobalStatus {
	    INACTIVE(0),
	    ACTIVE(1);

	    private final int value;

	    SSOUserGlobalStatus(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static SSOUserGlobalStatus fromInt(int i) {
	        for (SSOUserGlobalStatus status : values()) {
	            if (status.value == i) return status;
	        }
	        throw new IllegalArgumentException("Invalid SSOUserGlobalStatus value: " + i);
	    }
	}

	
	public enum HuertosUserRole {
	    USER(0),
	    ADMIN(1),
	    DEV(2);

	    private final int value;

	    HuertosUserRole(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static HuertosUserRole fromInt(int i) {
	        for (HuertosUserRole role : values()) {
	            if (role.value == i) return role;
	        }
	        throw new IllegalArgumentException("Invalid HuertosUserRole value: " + i);
	    }
	}

	
	public enum HuertosUserStatus {
	    INACTIVE(0),
	    ACTIVE(1);

	    private final int value;

	    HuertosUserStatus(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static HuertosUserStatus fromInt(int i) {
	        for (HuertosUserStatus status : values()) {
	            if (status.value == i) return status;
	        }
	        throw new IllegalArgumentException("Invalid HuertosUserStatus value: " + i);
	    }
	}

	
	public enum HuertoRequestStatus {
	    PENDING(0),
	    APPROVED(1),
	    REJECTED(2);

	    private final int value;

	    HuertoRequestStatus(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static HuertoRequestStatus fromInt(int i) {
	        for (HuertoRequestStatus status : values()) {
	            if (status.value == i) return status;
	        }
	        throw new IllegalArgumentException("Invalid HuertoRequestStatus value: " + i);
	    }
	}

	
	public enum MMCUserRole {
	    PLAYER(0);

	    private final int value;

	    MMCUserRole(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static MMCUserRole fromInt(int i) {
	        for (MMCUserRole role : values()) {
	            if (role.value == i) return role;
	        }
	        throw new IllegalArgumentException("Invalid MMCUserRole value: " + i);
	    }
	}

	
	public enum MMCUserStatus {
	    INACTIVE(0),
	    ACTIVE(1);

	    private final int value;

	    MMCUserStatus(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static MMCUserStatus fromInt(int i) {
	        for (MMCUserStatus status : values()) {
	            if (status.value == i) return status;
	        }
	        throw new IllegalArgumentException("Invalid MMCUserStatus value: " + i);
	    }
	}

	
	private Constants() {
        throw new AssertionError("Utility class cannot be instantiated.");
    }
}
