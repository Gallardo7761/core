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
	public static final String MEMBER_LIMITED_WAITLIST = Constants.HUERTOS_PREFIX + "/members/waitlist/limited";       // GET lista limitada
	public static final String LAST_MEMBER_NUMBER = Constants.HUERTOS_PREFIX + "/members/latest-number"; // GET último número de socio usado
	public static final String MEMBER_PROFILE = Constants.HUERTOS_PREFIX + "/members/profile"; // GET perfil del usuario logado (socio o admin)
	public static final String MEMBER_HAS_COLLABORATOR = Constants.HUERTOS_PREFIX + "/members/number/:member_number/has-collaborator"; // GET si tiene colaborador asignado
	public static final String MEMBER_HAS_COLLABORATOR_REQUEST = Constants.HUERTOS_PREFIX + "/members/number/:member_number/has-collaborator-request"; // GET si tiene solicitud de colaborador asignada
	public static final String MEMBER_HAS_GREENHOUSE = Constants.HUERTOS_PREFIX + "/members/number/:member_number/has-greenhouse"; // GET si tiene invernadero asignado
	public static final String MEMBER_HAS_GREENHOUSE_REQUEST = Constants.HUERTOS_PREFIX + "/members/number/:member_number/has-greenhouse-request"; // GET si tiene solicitud de invernadero asignada
	public static final String CHANGE_MEMBER_STATUS = Constants.HUERTOS_PREFIX + "/members/:user_id/status"; // PUT cambiar estado de socio (activo, inactivo, baja, etc.)
	public static final String CHANGE_MEMBER_TYPE = Constants.HUERTOS_PREFIX + "/members/:user_id/type"; // PUT cambiar tipo de socio (socio, colaborador, etc.)
	
	// ingresos -> GET, POST, PUT, DELETE
	public static final String INCOMES = Constants.HUERTOS_PREFIX + "/incomes";
	public static final String INCOMES_WITH_NAMES = Constants.HUERTOS_PREFIX + "/incomes-with-names";
	public static final String INCOME = Constants.HUERTOS_PREFIX + "/incomes/:income_id";
	public static final String MY_INCOMES = Constants.HUERTOS_PREFIX + "/incomes/my-incomes"; // GET ingresos del usuario logado (socio o admin)
	
	// gastos -> GET, POST, PUT, DELETE
	public static final String EXPENSES = Constants.HUERTOS_PREFIX + "/expenses";
	public static final String EXPENSE = Constants.HUERTOS_PREFIX + "/expenses/:expense_id";
	
	// balance -> GET, POST, PUT, DELETE
	public static final String BALANCE = Constants.HUERTOS_PREFIX + "/balance";
	public static final String BALANCE_WITH_TOTALS = Constants.HUERTOS_PREFIX + "/balance/with-totals";
	
	// anuncios -> GET, POST, PUT, DELETE
	public static final String ANNOUNCES = Constants.HUERTOS_PREFIX + "/announces";
	public static final String ANNOUNCE = Constants.HUERTOS_PREFIX + "/announces/:announce_id";
	
	// solicitudes -> GET, POST, PUT, DELETE
	public static final String REQUESTS = Constants.HUERTOS_PREFIX + "/requests";
	public static final String REQUEST = Constants.HUERTOS_PREFIX + "/requests/:request_id";
	public static final String REQUEST_COUNT = Constants.HUERTOS_PREFIX + "/requests/count"; // GET número de solicitudes
	public static final String MY_REQUESTS = Constants.HUERTOS_PREFIX + "/requests/my-requests"; // GET solicitudes del usuario logado (socio o admin)
	public static final String ACCEPT_REQUEST = Constants.HUERTOS_PREFIX + "/requests/:request_id/accept"; // PUT aceptar solicitud
	public static final String REJECT_REQUEST = Constants.HUERTOS_PREFIX + "/requests/:request_id/reject"; // PUT rechazar solicitud

	// pre-socios -> GET, POST, PUT, DELETE
	public static final String PRE_USERS = Constants.HUERTOS_PREFIX + "/pre_users";
	public static final String PRE_USER = Constants.HUERTOS_PREFIX + "/pre_users/:pre_user_id";	
	public static final String PRE_USER_VALIDATE = Constants.HUERTOS_PREFIX + "/pre_users/validate"; // POST validar pre-socio
	
	// solicitud + pre-socio -> GET
	public static final String REQUESTS_WITH_PRE_USERS = Constants.HUERTOS_PREFIX + "/requests-full";
	public static final String REQUEST_WITH_PRE_USER = Constants.HUERTOS_PREFIX + "/requests-full/:request_id";
}
