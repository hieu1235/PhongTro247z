package DBcontext;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBContext {

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=phongtro247_db");
            config.setUsername("sa");
            config.setPassword("123");
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Cấu hình pool (có thể chỉnh tùy theo nhu cầu)
            config.setMaximumPoolSize(10);   // tối đa 10 connection
            config.setMinimumIdle(2);        // giữ sẵn 2 connection rảnh
            config.setIdleTimeout(30000);    // 30s connection rảnh sẽ bị đóng
            config.setMaxLifetime(1800000);  // 30 phút sẽ refresh connection

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy connection từ pool
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
