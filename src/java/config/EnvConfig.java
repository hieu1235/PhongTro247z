package config;

/**
 * Lớp EnvConfig phiên bản ĐƠN GIẢN VÀ HIỆU QUẢ cho server.
 * Lớp này chỉ đọc trực tiếp từ biến môi trường của hệ thống (System.getenv).
 */
public class EnvConfig {

    /**
     * Phương thức trung tâm để lấy một biến môi trường.
     * @param key Tên của biến (ví dụ: "DB_HOST")
     * @return Giá trị của biến, hoặc null nếu không tồn tại.
     */
    private static String get(String key) {
        return System.getenv(key);
    }

    // ========== DATABASE CONFIGURATION ==========

    public static String getDatabaseUsername() {
        return get("DB_USERNAME");
    }

    public static String getDatabasePassword() {
        return get("DB_PASSWORD");
    }

    /**
     * Xây dựng chuỗi JDBC URL từ các biến môi trường riêng lẻ.
     * Đây là cách làm đúng chuẩn cho môi trường server.
     * @return Chuỗi JDBC URL hoàn chỉnh.
     */
    public static String buildDatabaseUrl() {
        String host = get("DB_HOST");
        String port = get("DB_PORT");
        String dbName = get("DB_NAME");

        // Kiểm tra để đảm bảo tất cả các biến cần thiết đều được thiết lập trên Render
        if (host == null || port == null || dbName == null) {
            System.err.println("FATAL ERROR: Database environment variables (DB_HOST, DB_PORT, DB_NAME) are NOT SET!");
            throw new IllegalStateException("Database configuration is missing!");
        }
        
        // Mẫu URL cho SQL Server, có thêm các tham số bảo mật cần thiết cho cloud.
        return "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbName + ";encrypt=true;trustServerCertificate=true;";
    }

    // ========== PAYOS CONFIGURATION ==========
    
    public static String getPayOSClientId() {
        return get("PAYOS_CLIENT_ID");
    }

    public static String getPayOSApiKey() {
        return get("PAYOS_API_KEY");
    }

    public static String getPayOSChecksumKey() {
        return get("PAYOS_CHECKSUM_KEY");
    }

    // ========== APPLICATION CONFIGURATION ==========
    
    public static String getAppBaseUrl() {
        return get("APP_BASE_URL");
    }

    public static boolean isProduction() {
        return "production".equalsIgnoreCase(get("APP_ENV"));
    }

    // ========== EMAIL CONFIGURATION ==========
    
    public static String getSmtpHost() {
        return get("SMTP_HOST");
    }

    public static String getSmtpPort() {
        return get("SMTP_PORT");
    }

    public static String getSmtpUsername() {
        return get("SMTP_USERNAME");
    }

    public static String getSmtpPassword() {
        return get("SMTP_PASSWORD");
    }

    // ========== FACEBOOK OAUTH CONFIGURATION ==========
    
    public static String getFacebookAppId() {
        return get("FACEBOOK_APP_ID");
    }

    public static String getFacebookAppSecret() {
        return get("FACEBOOK_APP_SECRET");
    }
}