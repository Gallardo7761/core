package net.miarma.api.microservices.mpaste.routing;

import net.miarma.api.common.Constants;

public class MPasteEndpoints {
	public static final String PASTES = Constants.MPASTE_PREFIX + "/pastes"; // GET, POST
	public static final String PASTE = Constants.MPASTE_PREFIX + "/pastes/:paste_id"; // GET, DELETE
	public static final String PASTE_BY_KEY = Constants.MPASTE_PREFIX + "/pastes/:paste_key"; // GET
}
