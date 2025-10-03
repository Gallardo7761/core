package net.miarma.api.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Gestión de toda la configuración de la aplicación.
 * Se encarga de cargar, guardar y proporcionar acceso a las propiedades de configuración.
 * Proporciona métodos para obtener la URL de la base de datos, directorios de archivos,
 * y propiedades específicas como host, puerto, etc.
 * <p>
 * Esta clase sigue el patron Singleton para asegurar una sola instancia.
 * @author José Manuel Amador Gallardo
 */
public class ConfigManager {
    private static final ConfigManager INSTANCE = new ConfigManager();
    private final File configFile;
    private final Properties config;
    private static final String CONFIG_FILE_NAME = "config.properties";

    private ConfigManager() {
    	String path = getBaseDir() + CONFIG_FILE_NAME;
        this.configFile = new File(path);
        this.config = new Properties();
    }

    public static synchronized ConfigManager getInstance() {
        return INSTANCE;
    }

    public void loadConfig() {
        try (FileInputStream fis = new FileInputStream(configFile);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            config.load(isr);
        } catch (IOException e) {
            Constants.LOGGER.error("Error loading configuration file: ", e);
        }
    }
    public File getConfigFile() {
		return configFile;
	}

    public String getJdbcUrl() {
        return String.format("%s://%s:%s/%s",
                config.getProperty("db.protocol"),
                config.getProperty("db.host"),
                config.getProperty("db.port"),
                config.getProperty("db.name"));
    }
    
    public String getHost() {
        return this.getStringProperty("inet.host");
    }
    
    public String getHomeDir() {
        if (isDocker()) {
            return "/data/";
        }
        return getOS() == OSType.WINDOWS ? 
                "C:/Users/" + System.getProperty("user.name") + "/" :
                System.getProperty("user.home").contains("root") ? "/root/" : 
                "/home/" + System.getProperty("user.name") + "/";
    }

    public String getBaseDir() {
        if (isDocker()) {
            return getHomeDir() + ".config/";
        }
        return getHomeDir() + (getOS() == OSType.WINDOWS ? ".miarmacoreapi/" :
                getOS() == OSType.LINUX ? ".config/miarmacoreapi/" :
                ".contaminus/");
    }

    public String getFilesDir(String context) {
        if (config.getProperty("files.dir") != null) {
            return config.getProperty("files.dir");
        }
        if (isDocker()) {
            return "/files/" + context + "/";
        }
        return getOS() == OSType.WINDOWS ?
                System.getProperty("user.home") + "\\" + "Documents\\" + context + "\\" :
                "/var/www/files/" + context + "/";
    }

    public String getModsDir() {
        return getFilesDir("miarmacraft") + "mods/";
    }

    public String getWebRoot() {
        if (config.getProperty("web.root") != null) {
            return config.getProperty("web.root");
        }
        return getBaseDir() + "webroot/";
    }


    public static OSType getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return OSType.WINDOWS;
        } else if (os.contains("nix") || os.contains("nux")) {
            return OSType.LINUX;
        } else {
            return OSType.INVALID_OS;
        }
    }
    
    public static boolean isDocker() {
    	return Boolean.parseBoolean(System.getenv("RUNNING_IN_DOCKER"));
    }
    
    public String getStringProperty(String key) {
        return config.getProperty(key);
    }
    
    public int getIntProperty(String key) {
        String value = config.getProperty(key);
        return value != null ? Integer.parseInt(value) : 10;
    }
    
    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(config.getProperty(key));
    }

    public void setProperty(String key, String value) {
        config.setProperty(key, value);
        saveConfig();
    }

    private void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            config.store(fos, "Configuration for: " + Constants.APP_NAME);
        } catch (IOException e) {
            Constants.LOGGER.error("Error saving configuration file: ", e);
        }
    }
}