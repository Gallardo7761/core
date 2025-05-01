package net.miarma.api.common;

import java.io.*;
import java.util.Properties;

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
        try (FileInputStream fis = new FileInputStream(configFile)) {
            config.load(fis);
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
    	return getOS() == OSType.WINDOWS ? 
                "C:/Users/" + System.getProperty("user.name") + "/" :
                System.getProperty("user.home").contains("root") ? "/root/" : 
                "/home/" + System.getProperty("user.name") + "/";
    }
    
    public String getBaseDir() {
		return getHomeDir() + 
				(getOS() == OSType.WINDOWS ? ".miarmacoreapi/" :
					getOS() == OSType.LINUX ? ".config/miarmacoreapi/" :
				".contaminus/");
	}
    
    public String getFilesDir(String context) {
    			return config.getProperty("files.dir") != null ?
						config.getProperty("files.dir") :
						(getOS() == OSType.WINDOWS ?
							System.getProperty("user.home") + "\\" + "Documents\\" + context + "\\" :
							getOS() == OSType.LINUX ?
								"/var/www/files/" + context + "/" :
									null);
    }
    
    public String getModsDir() {
    	return getFilesDir("miarmacraft") + "mods/";
    }
    
    public String getWebRoot() {
		return config.getProperty("web.root") != null ? 
				config.getProperty("web.root") : 
				getBaseDir() + "webroot/";
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