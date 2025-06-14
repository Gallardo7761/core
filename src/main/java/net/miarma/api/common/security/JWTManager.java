package net.miarma.api.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.microservices.core.entities.UserEntity;

import java.util.Date;

/**
 * Clase de gestión de JSON Web Tokens (JWT).
 * Proporciona métodos para generar, verificar y decodificar tokens JWT.
 * <p>
 * Esta clase sigue el patron Singleton para asegurar una sola instancia.
 *
 * @author José Manuel Amador Gallardo
 */
public class JWTManager {

	private final ConfigManager config = ConfigManager.getInstance();
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private static JWTManager instance;
    
    private JWTManager() {
		this.algorithm = Algorithm.HMAC256(config.getStringProperty("jwt.secret"));
		this.verifier = JWT.require(algorithm).build();
	}

    /**
     * Obtiene la instancia única de JWTManager.
     *
     * @return La instancia única de JWTManager.
     */
    public static synchronized JWTManager getInstance() {
		if (instance == null) {
			instance = new JWTManager();
		}
		return instance;
	}

    /**
     * Genera un token JWT para un usuario.
     *
     * @param user El usuario para el cual se generará el token.
     * @param keepLoggedIn Indica si el token debe tener una duración prolongada.
     * @return El token JWT generado.
     */
    public String generateToken(UserEntity user, boolean keepLoggedIn) {
    	final long EXPIRATION_TIME_MS = 1000L * (keepLoggedIn ? config.getIntProperty("jwt.expiration") : config.getIntProperty("jwt.expiration.short"));
    	return JWT.create()
    	        .withSubject(user.getUser_name())
    	        .withClaim("userId", user.getUser_id())
    	        .withClaim("isAdmin", user.getRole() == Constants.CoreUserRole.ADMIN)
    	        .withIssuedAt(new Date())
    	        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
    	        .sign(algorithm);
    }

    /**
     * Decodifica un token JWT sin verificar su firma.
     *
     * @param token El token JWT a decodificar.
     * @return Un objeto DecodedJWT que contiene la información del token.
     */
    public DecodedJWT decodeWithoutVerification(String token) {
        return JWT.decode(token);
    }

    /**
     * Verifica la validez de un token JWT.
     *
     * @param token El token JWT a verificar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean isValid(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si un token JWT pertenece a un usuario administrador.
     *
     * @param token El token JWT a verificar.
     * @return true si el token pertenece a un administrador, false en caso contrario.
     */
    public boolean isAdmin(String token) {
		try {
			DecodedJWT jwt = verifier.verify(token);
			return jwt.getClaim("isAdmin").asBoolean();
		} catch (Exception e) {
			return false;
		}
	}

    /**
     * Obtiene el ID de usuario a partir de un token JWT.
     *
     * @param token El token JWT del cual se extraerá el ID de usuario.
     * @return El ID de usuario si el token es válido, -1 en caso contrario.
     */
    public int getUserId(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("userId").asInt();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Obtiene el sub especificado en un token JWT, que generalmente es el nombre de usuario.
     *
     * @param token El token JWT del cual se extraerá el nombre de usuario.
     * @return El nombre de usuario si el token es válido, null en caso contrario.
     */
    public String getSubject(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrae el ID de usuario de un token JWT.
     * @param token El token JWT del cual se extraerá el ID de usuario.
     * @return El ID de usuario si el token es válido, -1 en caso contrario.
     */
	public int extractUserId(String token) {
		try {
			DecodedJWT jwt = verifier.verify(token);
			return jwt.getClaim("userId").asInt();
		} catch (Exception e) {
			return -1;
		}
	}
}
