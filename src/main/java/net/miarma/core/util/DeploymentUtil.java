package net.miarma.core.util;

public class DeploymentUtil {
	
	public static <T> String successMessage(Class<T> clazz) {
		return String.join(" ", "🟢", clazz.getName(), "deployed successfully");
	}
	
	public static <T> String failMessage(Class<T> clazz, Throwable e) {
		return String.join(" ", "🔴 Error deploying", clazz.getName()+":", e.getMessage());
	}
	
	public static String apiUrlMessage(String host, Integer port) {
		return String.join(" ", "\t🔗 API URL:", host+":"+port);
	}
}
