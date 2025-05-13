package net.miarma.api.core.routing;

import net.miarma.api.common.Constants;

public class CoreEndpoints {
	
	/*
	 * RUTAS DE LA API DE DATOS
	 * DE NEGOCIO DEL SSO
	 */
	
	// Usuarios
	public static final String USERS = Constants.CORE_PREFIX + "/users"; // GET, POST, PUT, DELETE
	public static final String USER = Constants.CORE_PREFIX + "/users/:user_id"; // GET, PUT, DELETE
	public static final String USER_STATUS = Constants.CORE_PREFIX + "/users/:user_id/status"; // GET, PUT
	public static final String USER_ROLE = Constants.CORE_PREFIX + "/users/:user_id/role"; // GET, PUT
	public static final String USER_EXISTS = Constants.CORE_PREFIX + "/users/:user_id/exists"; // GET
	public static final String USER_AVATAR = Constants.CORE_PREFIX + "/users/:user_id/avatar"; // GET, PUT
	public static final String USER_INFO = Constants.CORE_PREFIX + "/users/me"; // GET
	
	// Archivos
	public static final String FILES = Constants.CORE_PREFIX + "/files"; // GET, POST
	public static final String FILE = Constants.CORE_PREFIX + "/files/:file_id"; // GET, PUT, DELETE
	public static final String FILE_UPLOAD = Constants.CORE_PREFIX + "/files/upload"; // POST
	public static final String FILE_DOWNLOAD = Constants.CORE_PREFIX + "/files/:file_id/download"; // GET
	public static final String USER_FILES = Constants.CORE_PREFIX + "/files/myfiles"; // GET
	
	/*
	 * RUTAS DE LA API DE LOGICA 
	 * DE NEGOCIO DEL SSO
	 */
    public static final String LOGIN = Constants.AUTH_PREFIX + "/login"; // POST
    public static final String LOGIN_VALID = Constants.AUTH_PREFIX + "/login/validate"; // POST
    public static final String REGISTER = Constants.AUTH_PREFIX + "/register"; // POST
    public static final String CHANGE_PASSWORD = Constants.AUTH_PREFIX + "/change-password"; // POST
    public static final String VALIDATE_TOKEN = Constants.AUTH_PREFIX + "/validate-token"; // POST
    public static final String REFRESH_TOKEN = Constants.AUTH_PREFIX + "/refresh-token"; // POST
    public static final String SCREENSHOT = Constants.CORE_PREFIX + "/screenshot"; // GET
}
