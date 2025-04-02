package net.miarma.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String APP_NAME = "MiarmaCoreAPI";
	public static final String BASE_PREFIX = "/api";
	public static final String CORE_PREFIX = BASE_PREFIX + "/core/v1"; // tabla de usuarios central
	public static final String AUTH_PREFIX = "/auth/v1";
	public static final String HUERTOS_PREFIX = BASE_PREFIX + "/huertos/v1";
	public static final String MMC_PREFIX = BASE_PREFIX + "/mmc/v1";
	
    public static final String AUTH_EVENT_BUS = "auth.eventbus";
    public static final String CORE_EVENT_BUS = "core.eventbus";
    
    public static Logger LOGGER = LoggerFactory.getLogger(Constants.APP_NAME);
    
	public static final Integer CORE_LOGIC_PORT = 8080;
	public static final Integer CORE_DATA_PORT = 8081;
	public static final Integer MMC_LOGIC_PORT = 8100;
	public static final Integer MMC_DATA_PORT = 8101;
	public static final Integer HUERTOS_LOGIC_PORT = 8120;
	public static final Integer HUERTOS_DATA_PORT = 8121;
    
	public enum CoreUserRole implements ValuableEnum {
	    USER(0),
	    ADMIN(1);

	    private final int value;

	    CoreUserRole(int value) {
	        this.value = value;
	    }

	    @Override
	    public int getValue() {
	        return value;
	    }

	    public static CoreUserRole fromInt(int i) {
	        for (CoreUserRole role : values()) {
	            if (role.value == i) return role;
	        }
	        throw new IllegalArgumentException("Invalid CoreUserRole value: " + i);
	    }
	}

	
	public enum CoreUserGlobalStatus implements ValuableEnum {
	    INACTIVE(0),
	    ACTIVE(1);

	    private final int value;

	    CoreUserGlobalStatus(int value) {
	        this.value = value;
	    }

	    @Override
	    public int getValue() {
	        return value;
	    }

	    public static CoreUserGlobalStatus fromInt(int i) {
	        for (CoreUserGlobalStatus status : values()) {
	            if (status.value == i) return status;
	        }
	        throw new IllegalArgumentException("Invalid CoreUserGlobalStatus value: " + i);
	    }
	}
	
	public enum CoreFileContext implements ValuableEnum {
		CORE(0),
		HUERTOS(1),
		MMC(2);
		
		private final int value;
		
		CoreFileContext(int value) {
			this.value = value;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		public static CoreFileContext fromInt(int i) {
			for (CoreFileContext context : values()) {
				if (context.value == i) return context;
			}
			throw new IllegalArgumentException("Invalid CoreFileContext value: " + i);
		}
	}

	
	public enum HuertosUserRole implements ValuableEnum {
	    USER(0),
	    ADMIN(1),
	    DEV(2);

	    private final int value;

	    HuertosUserRole(int value) {
	        this.value = value;
	    }

	    @Override
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
	
	public enum HuertosUserType implements ValuableEnum {
		WAIT_LIST(0),
		MEMBER(1),
		WITH_GREENHOUSE(2),
		COLLABORATOR(3);
		
		private final int value;
		
		HuertosUserType(int value) {
			this.value = value;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		public static HuertosUserType fromInt(int i) {
			for (HuertosUserType type : values()) {
				if (type.value == i) return type;
			}
			throw new IllegalArgumentException("Invalid HuertosUserType value: " + i);
		}
	}

	
	public enum HuertosUserStatus implements ValuableEnum {
	    INACTIVE(0),
	    ACTIVE(1);

	    private final int value;

	    HuertosUserStatus(int value) {
	        this.value = value;
	    }

	    @Override
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

	
	public enum HuertosRequestStatus implements ValuableEnum {
	    PENDING(0),
	    APPROVED(1),
	    REJECTED(2);

	    private final int value;

	    HuertosRequestStatus(int value) {
	        this.value = value;
	    }

	    @Override
	    public int getValue() {
	        return value;
	    }

	    public static HuertosRequestStatus fromInt(int i) {
	        for (HuertosRequestStatus status : values()) {
	            if (status.value == i) return status;
	        }
	        throw new IllegalArgumentException("Invalid HuertoRequestStatus value: " + i);
	    }
	}
	
	public enum HuertosPaymentType implements ValuableEnum {
		BANK(0),
		CASH(1);
		
		private final int value;
		
		HuertosPaymentType(int value) {
			this.value = value;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		public static HuertosPaymentType fromInt(int i) {
			for (HuertosPaymentType type : values()) {
				if (type.value == i) return type;
			}
			throw new IllegalArgumentException("Invalid HuertoPaymentType value: " + i);
		}
	}
	
	public enum HuertosRequestType implements ValuableEnum {
		REGISTER(0),
		UNREGISTER(1),
		ADD_COLLABORATOR(2),
		REMOVE_COLLABORATOR(3);
		
		private final int value;
		
		HuertosRequestType(int value) {
			this.value = value;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		public static HuertosRequestType fromInt(int i) {
			for (HuertosRequestType type : values()) {
				if (type.value == i) return type;
			}
			throw new IllegalArgumentException("Invalid HuertoRequestType value: " + i);
		}
	}
	
	public enum HuertosAnnouncePriority implements ValuableEnum {
		LOW(0),
		MEDIUM(1),
		HIGH(2);
		
		private final int value;
		
		HuertosAnnouncePriority(int value) {
			this.value = value;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		public static HuertosAnnouncePriority fromInt(int i) {
			for (HuertosAnnouncePriority priority : values()) {
				if (priority.value == i) return priority;
			}
			throw new IllegalArgumentException("Invalid HuertoAnnouncePriority value: " + i);
		}
	}
	
	public enum HuertosPaymentFrequency implements ValuableEnum {
		BIYEARLY(0),
		YEARLY(1);
		
		private final int value;
		
		HuertosPaymentFrequency(int value) {
			this.value = value;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		public static HuertosPaymentFrequency fromInt(int i) {
			for (HuertosPaymentFrequency frequency : values()) {
				if (frequency.value == i) return frequency;
			}
			throw new IllegalArgumentException("Invalid HuertoPaymentFrequency value: " + i);
		}
	}

	
	public enum MMCUserRole implements ValuableEnum {
	    PLAYER(0);

	    private final int value;

	    MMCUserRole(int value) {
	        this.value = value;
	    }

	    @Override
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

	
	public enum MMCUserStatus implements ValuableEnum {
	    INACTIVE(0),
	    ACTIVE(1);

	    private final int value;

	    MMCUserStatus(int value) {
	        this.value = value;
	    }

	    @Override
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
