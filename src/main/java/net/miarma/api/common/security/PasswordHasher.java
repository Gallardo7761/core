package net.miarma.api.common.security;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private static final int SALT_ROUNDS = 12;

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
