package net.miarma.api.huertos.api;

import net.miarma.api.common.Constants;

public class HuertosEndpoints {
	// auth
	public static final String LOGIN = Constants.HUERTOS_PREFIX + "/login";
	
	// socios -> GET, POST, PUT, DELETE
	public static final String USERS = Constants.HUERTOS_PREFIX + "/users";
	public static final String USER = Constants.HUERTOS_PREFIX + "/users/:user_id";
	public static final String USER_BY_MEMBER_NUMBER = Constants.HUERTOS_PREFIX + "/users/member/:member_number";
	public static final String USER_BY_PLOT_NUMBER = Constants.HUERTOS_PREFIX + "/users/plot/:plot_number";
	public static final String USER_BY_DNI = Constants.HUERTOS_PREFIX + "/users/dni/:dni";
	public static final String USER_PAYMENTS = Constants.HUERTOS_PREFIX + "/users/:member_number/incomes";
	public static final String USER_HAS_PAID = Constants.HUERTOS_PREFIX + "/users/:member_number/haspaid";
	public static final String USER_WAITLIST = Constants.HUERTOS_PREFIX + "/users/waitlist";
	
	// ingresos -> GET, POST, PUT, DELETE
	public static final String INCOMES = Constants.HUERTOS_PREFIX + "/incomes";
	public static final String INCOME = Constants.HUERTOS_PREFIX + "/incomes/:income_id";
	
	// gastos -> GET, POST, PUT, DELETE
	public static final String EXPENSES = Constants.HUERTOS_PREFIX + "/expenses";
	public static final String EXPENSE = Constants.HUERTOS_PREFIX + "/expenses/:expense_id";
	
	// balance -> GET, POST, PUT, DELETE
	public static final String BALANCE = Constants.HUERTOS_PREFIX + "/balance";
	
	// anuncios -> GET, POST, PUT, DELETE
	public static final String ANNOUNCES = Constants.HUERTOS_PREFIX + "/announces";
	public static final String ANNOUNCE = Constants.HUERTOS_PREFIX + "/announces/:announce_id";
	
	// solicitudes -> GET, POST, PUT, DELETE
	public static final String REQUESTS = Constants.HUERTOS_PREFIX + "/requests";
	public static final String REQUEST = Constants.HUERTOS_PREFIX + "/requests/:request_id";

	// pre-socios -> GET, POST, PUT, DELETE
	public static final String PRE_USERS = Constants.HUERTOS_PREFIX + "/pre_users";
	public static final String PRE_USER = Constants.HUERTOS_PREFIX + "/pre_users/:pre_user_id";	
}
