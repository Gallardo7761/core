package net.miarma.core.sso.api;

import net.miarma.core.common.Constants;

public class SSOEndpoints {
	
	/*
	 * RUTAS DE LA API DE DATOS
	 * DE NEGOCIO DEL SSO
	 */
	
	// Usuarios
	public static final String USERS = Constants.API_PREFIX + "/users"; // GET, POST, PUT, DELETE
	public static final String USER = Constants.API_PREFIX + "/users/:user_id"; // GET, PUT, DELETE
	public static final String USER_STATUS = Constants.API_PREFIX + "/users/:user_id/status"; // GET, PUT
	public static final String USER_ROLE = Constants.API_PREFIX + "/users/:user_id/role"; // GET, PUT
	public static final String USER_BY_EMAIL = Constants.API_PREFIX + "/users/email/:email"; // GET
	public static final String USER_BY_USERNAME = Constants.API_PREFIX + "/users/username/:username"; // GET
	public static final String USER_INFO = Constants.API_PREFIX + "/users/me"; // GET
	public static final String USER_EXISTS = Constants.API_PREFIX + "/users/:user_id/exists"; // GET
	public static final String USER_AVATAR = Constants.API_PREFIX + "/users/:user_id/avatar"; // GET, PUT
	
	
	/*
	 * RUTAS DE LA API DE LOGICA 
	 * DE NEGOCIO DEL SSO
	 */
	
    // Auth básica
    public static final String LOGIN = Constants.SSO_PREFIX + "/login"; // POST
    public static final String LOGOUT = Constants.SSO_PREFIX + "/logout"; // POST 
    public static final String REFRESH = Constants.SSO_PREFIX + "/refresh"; // POST

    // Registro y gestión de cuenta
    public static final String REGISTER = Constants.SSO_PREFIX + "/register"; // POST
    public static final String VERIFY_EMAIL = Constants.SSO_PREFIX + "/verify-email"; // POST
    public static final String CHANGE_PASSWORD = Constants.SSO_PREFIX + "/change-password"; // POST
    public static final String RESET_PASSWORD_REQUEST = Constants.SSO_PREFIX + "/reset-password/request"; // POST
    public static final String RESET_PASSWORD_CONFIRM = Constants.SSO_PREFIX + "/reset-password/confirm"; // POST

    // Token y datos del usuario
    public static final String VALIDATE_TOKEN = Constants.SSO_PREFIX + "/validate-token"; // POST
}
