package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Utility class để load environment variables từ file .env hoặc system environment
 * Sử dụng để lưu trữ credentials an toàn, không hard-code trong code
 */
public class EnvConfig {
    private static final Logger logger = Logger.getLogger(EnvConfig.class.getName());
    private static Properties properties = new Properties();
    private static boolean initialized = false;

    static {
        loadEnvironment();
    }

    /**
     * Load environment variables từ .env file hoặc system environment
     */
    private static void loadEnvironment() {
        if (initialized) {
            return;
        }

        // Danh sách các đường dẫn có thể chứa file .env
        String[] possiblePaths = {
            System.getProperty("user.dir") + "/.env",                    // Thư mục gốc dự án
            System.getProperty("user.dir") + "/web/.env",               // Thư mục web của dự án
            System.getProperty("user.dir") + "/build/web/.env",         // Thư mục build web
            System.getProperty("user.dir") + "/src/.env",               // Thư mục src (fallback)
            System.getProperty("catalina.home") + "/.env",              // Thư mục Tomcat (nếu có)
            System.getProperty("catalina.base") + "/.env"               // Thư mục instance Tomcat
        };

        // Thử load từ các đường dẫn có thể
        for (String envFilePath : possiblePaths) {
            try {
                FileInputStream fis = new FileInputStream(envFilePath);
                properties.load(fis);
                fis.close();
                logger.info("Loaded configuration from .env file at: " + envFilePath);
                initialized = true;
                return; // Thành công, thoát
            } catch (IOException e) {
                // Tiếp tục thử đường dẫn tiếp theo
                logger.fine("Could not load .env from: " + envFilePath + " - " + e.getMessage());
            }
        }

        // Nếu không tìm thấy file .env ở bất kỳ đường dẫn nào
        logger.info("No .env file found in any expected location, will use system environment variables");
        initialized = true;
    }

    /**
     * Get environment variable với fallback về default value
     * 
     * @param key Tên của environment variable
     * @param defaultValue Giá trị mặc định nếu không tìm thấy
     * @return Giá trị của environment variable
     */
    public static String get(String key, String defaultValue) {
        // Thử lấy từ .env file trước
        String value = properties.getProperty(key);
        
        // Nếu không có, thử lấy từ system environment
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        
        // Nếu vẫn không có, dùng default value
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        
        return value;
    }

    /**
     * Get environment variable (bắt buộc phải có, không có default)
     * Throw exception nếu không tìm thấy
     * 
     * @param key Tên của environment variable
     * @return Giá trị của environment variable
     * @throws IllegalStateException nếu không tìm thấy variable
     */
    public static String getRequired(String key) {
        String value = get(key, null);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                "Required environment variable not found: " + key + 
                ". Please set it in .env file or system environment."
            );
        }
        return value;
    }

    /**
     * Check xem có đang chạy ở production mode không
     */
    public static boolean isProduction() {
        return "production".equalsIgnoreCase(get("APP_ENV", "development"));
    }

    /**
     * Check xem có đang chạy ở development mode không
     */
    public static boolean isDevelopment() {
        return !isProduction();
    }

    // ========== DATABASE CONFIGURATION ==========
    
    public static String getDatabaseUrl() {
        return get("DB_URL", "jdbc:sqlserver://localhost:1433;databaseName=phongtro247_db");
    }

    public static String getDatabaseUsername() {
        return get("DB_USERNAME", "phongtro247_user");
    }

    public static String getDatabasePassword() {
        return get("DB_PASSWORD", "Komk@2004");
    }

    // ========== PAYOS CONFIGURATION ==========
    
    public static String getPayOSClientId() {
        return get("PAYOS_CLIENT_ID", "0527f12e-5e89-4803-ae9b-f5c2189b670f");
    }

    public static String getPayOSApiKey() {
        return get("PAYOS_API_KEY", "ddcfc131-ee08-4380-a16a-0593431a338b");
    }

    public static String getPayOSChecksumKey() {
        return get("PAYOS_CHECKSUM_KEY", "e55d8e0a8ad936d0ff2a9890748c55a4eea62b92b0739dc3dbfa73fdfe5da859");
    }

    // ========== APPLICATION CONFIGURATION ==========
    
    public static String getAppBaseUrl() {
        return get("APP_BASE_URL", "http://localhost:8080/PhongTroNew");
    }

    // ========== EMAIL CONFIGURATION ==========
    
    public static String getSmtpHost() {
        return get("SMTP_HOST", "smtp.gmail.com");
    }

    public static int getSmtpPort() {
        return Integer.parseInt(get("SMTP_PORT", "587"));
    }

    public static String getSmtpUsername() {
        return get("SMTP_USERNAME", "");
    }

    public static String getSmtpPassword() {
        return get("SMTP_PASSWORD", "");
    }

    // ========== FACEBOOK OAUTH CONFIGURATION ==========
    
    public static String getFacebookAppId() {
        return get("FACEBOOK_APP_ID", "");
    }

    public static String getFacebookAppSecret() {
        return get("FACEBOOK_APP_SECRET", "");
    }

    /**
     * Reload environment variables (useful for testing)
     */
    public static void reload() {
        properties.clear();
        initialized = false;
        loadEnvironment();
    }
}
