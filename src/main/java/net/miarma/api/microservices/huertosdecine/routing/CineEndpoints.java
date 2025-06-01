package net.miarma.api.microservices.huertosdecine.routing;

import net.miarma.api.common.Constants;

public class CineEndpoints {
    public static final String LOGIN = Constants.CINE_PREFIX + "/login";

    public static final String MOVIES = Constants.CINE_PREFIX + "/movies"; // GET, POST, PUT, DELETE
    public static final String MOVIE = Constants.CINE_PREFIX + "/movies/:movie_id"; // GET, PUT, DELETE

    public static final String VIEWERS = Constants.CINE_PREFIX + "/viewers"; // GET, POST, PUT, DELETE
    public static final String VIEWER = Constants.CINE_PREFIX + "/viewers/:viewer_id"; // GET, PUT, DELETE

    // logic layer
    public static final String MOVIE_VOTES = Constants.CINE_PREFIX + "/movies/:movie_id/votes"; // GET, POST, PUT, DELETE
    public static final String SELF_VOTE = Constants.CINE_PREFIX + "/movies/:movie_id/votes/self"; // GET
    public static final String VIEWER_VOTES_BY_MOVIE = Constants.CINE_PREFIX + "/viewers/:viewer_id/votes/:movie_id"; // GET

}
