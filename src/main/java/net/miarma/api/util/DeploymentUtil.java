package net.miarma.api.util;

/**
 * Clase de utilidad para mensajes de despliegue.
 * @author José Manuel Amador Gallardo
 */
public class DeploymentUtil {
	
	public static <T> String successMessage(Class<T> clazz) {
		return String.join(" ", "🟢", clazz.getSimpleName(), "deployed successfully");
	}
	
	public static <T> String failMessage(Class<T> clazz, Throwable e) {
		return String.join(" ", "🔴 Error deploying", clazz.getSimpleName()+":", e.getMessage());
	}
	
	public static String apiUrlMessage(String host, Integer port) {
		return String.join(" ", "\t🔗 API URL:", host+":"+port);
	}
}
