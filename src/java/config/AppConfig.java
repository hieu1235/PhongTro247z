package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static Properties properties = new Properties();
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("config/app.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Lỗi load app.properties: " + e.getMessage());
        }
    }
    
    public static String get(String key) {
        return properties.getProperty(key);
    }
    
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getInt(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    // Specific config getters
    public static String getDatabaseUrl() {
        return get("db.url", "jdbc:sqlserver://localhost:1433;databaseName=PhongTroNew");
    }
    
    public static String getDatabaseUsername() {
        return get("db.username", "sa");
    }
    
    public static String getDatabasePassword() {
        return get("db.password", "123");
    }
    
    public static String getAppBaseUrl() {
        return get("app.base.url", "https://phongtro247.com");
    }
    
    public static String getFacebookApiVersion() {
        return get("facebook.api.version", "v18.0");
    }
    
    public static int getAutoPostJobIntervalMinutes() {
        return getInt("autopost.job.interval.minutes", 5);
    }
    
    public static int getAutoPostDelayBetweenPostsSeconds() {
        return getInt("autopost.job.delay.between.posts.seconds", 2);
    }
}