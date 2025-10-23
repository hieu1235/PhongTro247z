package DBcontext;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.EnvConfig;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBContext {

    private static final Logger logger = Logger.getLogger(DBContext.class.getName());
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            
            config.setJdbcUrl(EnvConfig.buildDatabaseUrl()); 
            config.setUsername(EnvConfig.getDatabaseUsername());
            config.setPassword(EnvConfig.getDatabasePassword());

            // ✅ ĐÃ THAY ĐỔI: Sử dụng driver cho PostgreSQL
            config.setDriverClassName("org.postgresql.Driver");

            // Cấu hình Connection Pool
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(20000);
            
            dataSource = new HikariDataSource(config);
            
            logger.info("Database connection pool initialized successfully.");

        } catch (Exception e) {
            logger.severe("FATAL: Failed to initialize database connection pool: " + e.getMessage());
            e.printStackTrace(); 
            throw new RuntimeException("Cannot initialize database connection pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized. Check logs for initialization errors.");
        }
        return dataSource.getConnection();
    }
    
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed.");
        }
    }
}