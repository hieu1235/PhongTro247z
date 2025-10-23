package DBcontext;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing database...");
        try {
            initializeDatabase();
            logger.info("Database initialization completed successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }

    private void initializeDatabase() throws Exception {
        // Read SQL script from resources
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database/phongtro247_postgresql_database.sql");
        if (inputStream == null) {
            throw new RuntimeException("Database initialization script not found: database/phongtro247_postgresql_database.sql");
        }

        StringBuilder sqlScript = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sqlScript.append(line).append("\n");
            }
        }

        // Split script into individual statements
        String[] statements = sqlScript.toString().split(";");

        // Execute each statement
        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty()) {
                    logger.info("Executing: " + statement.substring(0, Math.min(50, statement.length())) + "...");
                    stmt.execute(statement);
                }
            }
        }
    }
}