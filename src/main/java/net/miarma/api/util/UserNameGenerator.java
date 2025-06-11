package net.miarma.api.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static void main(String[] args) {
        String baseName = "antonio";
        String input = "ANTONIO GARCÍA MORENO";

        try {
            String userName = generateUserName(baseName, input, 3); // 3 bytes = 6 hex chars
            System.out.println(userName);  // Ej: antonio-4f7a9b
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 no está disponible en este sistema");
        }
    }
}

