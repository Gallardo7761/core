package net.miarma.api.common;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.json.JsonObject;
import net.miarma.api.common.gson.APIDontReturnExclusionStrategy;
import net.miarma.api.common.gson.JsonObjectTypeAdapter;
import net.miarma.api.common.gson.LocalDateTimeAdapter;
import net.miarma.api.common.gson.ValuableEnumDeserializer;
import net.miarma.api.common.gson.ValuableEnumTypeAdapter;

/**
 * Clase que contiene constantes y enumeraciones utilizadas en la API de MiarmaCore.
 * @author José Manuel Amador Gallardo
 */
public class Constants {
	public static final String APP_NAME = "MiarmaCoreAPI";
	public static final String BASE_PREFIX = "/api";
	public static final String CORE_PREFIX = BASE_PREFIX + "/core/v1"; // tabla de usuarios central
	public static final String AUTH_PREFIX = "/auth/v1";
	public static final String HUERTOS_PREFIX = BASE_PREFIX + "/huertos/v1";
	public static final String MMC_PREFIX = BASE_PREFIX + "/mmc/v1";
	public static final String CINE_PREFIX = BASE_PREFIX + "/cine/v1";
	public static final String MPASTE_PREFIX = BASE_PREFIX + "/mpaste/v1";

    public static final String AUTH_EVENT_BUS = "auth.eventbus";
    public static final String CORE_EVENT_BUS = "core.eventbus";
    public static final String HUERTOS_EVENT_BUS = "huertos.eventbus";
    public static final String MMC_EVENT_BUS = "mmc.eventbus";
    public static final String CINE_EVENT_BUS = "cine.eventbus";
    public static final String MPASTE_EVENT_BUS = "mpaste.eventbus";
    
    public static final List<String> HUERTOS_ALLOWED_FOLDERS = 
    		List.of("INBOX", "Drafts", "Sent", "Spam", "Trash");
    
    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.APP_NAME);
	
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
			.registerTypeAdapter(JsonObject.class, new JsonObjectTypeAdapter())
			.registerTypeHierarchyAdapter(ValuableEnum.class, new ValuableEnumTypeAdapter())
			.registerTypeAdapter(CoreUserGlobalStatus.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(CoreUserRole.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(HuertosUserType.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(HuertosUserStatus.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(HuertosUserRole.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(HuertosRequestStatus.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(HuertosRequestType.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(HuertosPaymentType.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(HuertosPaymentFrequency.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(HuertosAnnouncePriority.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(MMCUserStatus.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(MMCUserRole.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(CoreFileContext.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(MMCModStatus.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(CineUserRole.class, new ValuableEnumDeserializer())
			.registerTypeAdapter(CineUserStatus.class, new ValuableEnumDeserializer())
			.addSerializationExclusionStrategy(new APIDontReturnExclusionStrategy())
			.create();
    
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
		MMC(2),
		CINE(3);
		
		private final int value;
		
		CoreFileContext(int value) {
			this.value = value;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		public String toCtxString() {
			return switch(this) {
				case CORE -> "core";
				case HUERTOS -> "huertos";
				case MMC -> "miarmacraft";
				case CINE -> "cine";
			};
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
		COLLABORATOR(3),
		DEVELOPER(5),
		SUBSIDY(4);
		
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
		REMOVE_COLLABORATOR(3),
		ADD_GREENHOUSE(4),
		REMOVE_GREENHOUSE(5);
		
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
	    PLAYER(0),
		ADMIN(1);

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
	
	public enum MMCModStatus implements ValuableEnum {
		ACTIVE(0),
		INACTIVE(1);
		
		private final int value;
		
		MMCModStatus(int value) {
			this.value = value;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		public static MMCModStatus fromInt(int i) {
			for (MMCModStatus status : values()) {
				if (status.value == i) return status;
			}
			throw new IllegalArgumentException("Invalid MiarmacraftModStatus value: " + i);
		}
	}

	public enum CineUserStatus implements ValuableEnum {
		ACTIVE(1),
		INACTIVE(0);

		private final int value;

		CineUserStatus(int value) {
			this.value = value;
		}

		@Override
		public int getValue() {
			return value;
		}

		public static CineUserStatus fromInt(int i) {
			for (CineUserStatus status : values()) {
				if (status.value == i) return status;
			}
			throw new IllegalArgumentException("Invalid CineUserStatus value: " + i);
		}
	}

	public enum CineUserRole implements ValuableEnum {
		USER(0),
		ADMIN(1);

		private final int value;

		CineUserRole(int value) {
			this.value = value;
		}

		@Override
		public int getValue() {
			return value;
		}

		public static CineUserRole fromInt(int i) {
			for (CineUserRole role : values()) {
				if (role.value == i) return role;
			}
			throw new IllegalArgumentException("Invalid CineUserRole value: " + i);
		}
	}

	// Private constructor to prevent instantiation 
	private Constants() {
        throw new AssertionError("Utility class cannot be instantiated.");
    }
}
