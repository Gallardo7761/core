package net.miarma.api.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase de utilidad para generar nombres de usuario únicos basados en un hash.
 * Utiliza SHA-256 para crear un hash del nombre de usuario y lo convierte a hexadecimal.
 *
 * @author José Manuel Amador Gallardo
 */
public class UserNameGenerator {
    public static String generateUserName(String baseName, String input, int hashBytesCount) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexHash = new StringBuilder();
        for (int i = 0; i < hashBytesCount && i < hash.length; i++) {
            hexHash.append(String.format("%02x", hash[i]));
        }

        return baseName + "-" + hexHash.toString();
    }
}

