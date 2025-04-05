package net.miarma.api.huertos.routing;

import net.miarma.api.common.Constants;

public class HuertosEndpoints {
	// auth
	public static final String LOGIN = Constants.HUERTOS_PREFIX + "/login";
	
	// socios -> GET, POST, PUT, DELETE
	public static final String MEMBERS = Constants.HUERTOS_PREFIX + "/members";                        // GET, POST, PUT, DELETE
	public static final String MEMBER = Constants.HUERTOS_PREFIX + "/members/:user_id";        // GET, POST, PUT, DELETE por id
	public static final String MEMBER_BY_NUMBER = Constants.HUERTOS_PREFIX + "/members/number/:member_number"; // GET por número de socio
	public static final String MEMBER_BY_PLOT = Constants.HUERTOS_PREFIX + "/members/plot/:plot_number";      // GET por número de parcela
	public static final String MEMBER_BY_DNI = Constants.HUERTOS_PREFIX + "/members/dni/:dni";         // GET por DNI
	public static final String MEMBER_PAYMENTS = Constants.HUERTOS_PREFIX + "/members/number/:member_number/incomes"; // GET ingresos de ese miembro
	public static final String MEMBER_HAS_PAID = Constants.HUERTOS_PREFIX + "/members/number/:member_number/has-paid"; // GET si ha pagado
	public static final String MEMBER_WAITLIST = Constants.HUERTOS_PREFIX + "/members/waitlist";       // GET todos los de la lista de espera
	public static final String LAST_MEMBER_NUMBER = Constants.HUERTOS_PREFIX + "/members/number/last-number"; // GET último número de socio usado
	
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
