package DBcontext;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.EnvConfig;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Database Context với connection pooling sử dụng HikariCP
 * 
 * ✅ SECURITY FIX: Credentials được load từ environment variables
 * thay vì hard-code trong code
 */
public class DBContext {

    private static final Logger logger = Logger.getLogger(DBContext.class.getName());
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            
            // ✅ SECURE: Load credentials từ environment variables
            config.setJdbcUrl(EnvConfig.getDatabaseUrl());
            config.setUsername(EnvConfig.getDatabaseUsername());
            config.setPassword(EnvConfig.getDatabasePassword());
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Connection pool configuration
            config.setMaximumPoolSize(10);   // tối đa 10 connection
            config.setMinimumIdle(2);        // giữ sẵn 2 connection rảnh
            config.setIdleTimeout(30000);    // 30s connection rảnh sẽ bị đóng
            config.setMaxLifetime(1800000);  // 30 phút sẽ refresh connection
            config.setConnectionTimeout(20000); // 20s timeout khi lấy connection
            
            // Security settings
            config.setLeakDetectionThreshold(60000); // Detect connection leaks sau 60s
            
            dataSource = new HikariDataSource(config);
            
            logger.info("Database connection pool initialized successfully");

        } catch (Exception e) {
            logger.severe("Failed to initialize database connection pool: " + e.getMessage());
            throw new RuntimeException("Cannot initialize database connection pool", e);
        }
    }

    /**
     * Lấy connection từ pool
     * 
     * @return Database connection
     * @throws SQLException nếu không thể lấy connection
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Đóng connection pool (call khi shutdown application)
     */
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}
