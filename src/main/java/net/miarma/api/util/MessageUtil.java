package net.miarma.api.util;

public class MessageUtil {
	public static String notFound(String what, String where) {
		return String.join(" ", "❌", what, "not found in", where);
	}
	
	public static String failedTo(String action, String on, Throwable e) {
		return String.join(" ", "❌ Failed to", action, on+":", e.getMessage());
	}
}
