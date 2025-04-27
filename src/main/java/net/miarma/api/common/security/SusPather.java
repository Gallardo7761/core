package net.miarma.api.common.security;

public class SusPather {
	public static boolean isSusPath(String path) {
		return path.endsWith(".env") ||
		           path.endsWith(".git") ||
		           path.endsWith(".DS_Store") ||
		           path.endsWith("wp-login.php") ||
		           path.endsWith("admin.php") ||
		           path.contains(".git/") ||
		           path.contains(".svn/") ||
		           path.contains(".idea/") ||
		           path.contains(".vscode/") ||
		           path.contains(".settings/") ||
		           path.contains(".classpath") ||
		           path.contains(".project");
	}
}
