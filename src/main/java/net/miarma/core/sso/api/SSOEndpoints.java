package net.miarma.core.sso.api;

public class SSOEndpoints {
	
	/*
	 * RUTAS DE LA API DE DATOS
	 * DE NEGOCIO DEL SSO
	 */
	
	// Usuarios
	public static final String USERS = "/users"; // GET, POST, PUT, DELETE
	public static final String USER = "/users/:user_id"; // GET, PUT, DELETE
	public static final String USER_STATUS = "/users/:user_id/status"; // GET, PUT
	public static final String USER_ROLES = "/users/:user_id/roles"; // GET, POST, DELETE
	public static final String USER_BY_EMAIL = "/users/email/:email"; // GET
	public static final String USER_BY_USERNAME = "/users/username/:username"; // GET
	public static final String USER_INFO = "/users/me"; // GET
	public static final String USER_EXISTS = "/users/exists"; // GET
	public static final String USER_AVATAR = "/users/:user_id/avatar"; // GET, PUT
	
	
	/*
	 * RUTAS DE LA API DE LOGICA 
	 * DE NEGOCIO DEL SSO
	 */
	
    // Auth básica
    public static final String LOGIN = "/login"; // POST
    public static final String LOGOUT = "/logout"; // POST 
    public static final String REFRESH = "/refresh"; // POST

    // Registro y gestión de cuenta
    public static final String REGISTER = "/register"; // POST
    public static final String VERIFY_EMAIL = "/verify-email"; // POST
    public static final String CHANGE_PASSWORD = "/change-password"; // POST
    public static final String RESET_PASSWORD_REQUEST = "/reset-password/request"; // POST
    public static final String RESET_PASSWORD_CONFIRM = "/reset-password/confirm"; // POST

    // Token y datos del usuario
    public static final String VALIDATE_TOKEN = "/validate-token"; // POST
}
