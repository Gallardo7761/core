package net.miarma.core.huertos.api;

import net.miarma.core.common.Constants;

public class HuertosEndpoints {
	// socios -> GET, POST, PUT, DELETE
	public static final String USERS = Constants.API_PREFIX + "/users";
	public static final String USER = Constants.API_PREFIX + "/users/:user_id";
	
	// ingresos -> GET, POST, PUT, DELETE
	public static final String INCOMES = Constants.API_PREFIX + "/incomes";
	public static final String INCOME = Constants.API_PREFIX + "/incomes/:income_id";
	
	// gastos -> GET, POST, PUT, DELETE
	public static final String EXPENSES = Constants.API_PREFIX + "/expenses";
	public static final String EXPENSE = Constants.API_PREFIX + "/expenses/:expense_id";
	
	// balance -> GET, POST
	public static final String BALANCE = Constants.API_PREFIX + "/balance";
	
	// anuncios -> GET, POST, PUT, DELETE
	public static final String ANNOUNCES = Constants.API_PREFIX + "/announces";
	public static final String ANNOUNCE = Constants.API_PREFIX + "/announces/:announce_id";
	
	// solicitudes -> GET, POST, PUT, DELETE
	public static final String REQUESTS = Constants.API_PREFIX + "/requests";
	public static final String REQUEST = Constants.API_PREFIX + "/requests/:request_id";
	
}
