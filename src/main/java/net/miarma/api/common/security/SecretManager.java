package net.miarma.api.common.security;

import net.miarma.api.common.ConfigManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;

public class SecretManager {

    private static String cachedSecret = null;

    public static String getOrCreateSecret() {
        if (cachedSecret != null) return cachedSecret;

        try {
            File configFile = ConfigManager.getInstance().getConfigFile();
            Properties config = new Properties();

            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    config.load(fis);
                }
            }

            String secret = config.getProperty("jwt.secret");
            if (secret != null && !secret.trim().isEmpty()) {
                cachedSecret = secret.trim();
            } else {
                cachedSecret = generateSecret(64);

                List<String> lines = Files.readAllLines(configFile.toPath());

                boolean replaced = false;
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).trim().startsWith("jwt.secret=")) {
                        lines.set(i, "jwt.secret=" + cachedSecret);
                        replaced = true;
                        break;
                    }
                }

                if (!replaced) {
                    lines.add("# Security Configuration");
                    lines.add("jwt.secret=" + cachedSecret);
                }

                Files.write(configFile.toPath(), lines);
            }

            return cachedSecret;

        } catch (IOException e) {
            throw new RuntimeException("Could not create or get the secret", e);
        }
    }

    private static String generateSecret(int byteLength) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
