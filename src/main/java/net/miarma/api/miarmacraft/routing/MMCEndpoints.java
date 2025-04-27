package net.miarma.api.miarmacraft.routing;

import net.miarma.api.common.Constants;

public class MMCEndpoints {
	public static final String LOGIN = Constants.MMC_PREFIX + "/login"; // POST
	
	public static final String MODS = Constants.MMC_PREFIX + "/mods"; // GET, POST, PUT, DELETE
	public static final String MOD = Constants.MMC_PREFIX + "/mods/:mod_id"; // GET, PUT, DELETE
	public static final String MOD_STATUS = Constants.MMC_PREFIX + "/mods/:mod_id/status"; // GET, PUT
	
	public static final String PLAYERS = Constants.MMC_PREFIX + "/players"; // GET, POST, PUT, DELETE
	public static final String PLAYER = Constants.MMC_PREFIX + "/players/:player_id"; // GET, PUT, DELETE
	public static final String PLAYER_STATUS = Constants.MMC_PREFIX + "/players/:player_id/status"; // GET, PUT
	public static final String PLAYER_ROLE = Constants.MMC_PREFIX + "/players/:player_id/role"; // GET, PUT
	public static final String PLAYER_EXISTS = Constants.MMC_PREFIX + "/players/:player_id/exists"; // GET
	public static final String PLAYER_AVATAR = Constants.MMC_PREFIX + "/players/:player_id/avatar"; // GET, PUT
	public static final String PLAYER_INFO = Constants.MMC_PREFIX + "/players/me"; // GET
}