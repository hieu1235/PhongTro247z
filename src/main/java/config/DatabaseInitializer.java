package config;

import DBcontext.DBContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Logger;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing database...");

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if tables already exist
            boolean tablesExist = false;
            try {
                stmt.executeQuery("SELECT 1 FROM roles LIMIT 1");
                tablesExist = true;
                logger.info("Database tables already exist, skipping initialization.");
            } catch (Exception e) {
                logger.info("Database tables don't exist, initializing...");
            }

            if (!tablesExist) {
                // Read and execute the database initialization script
                InputStream is = getClass().getClassLoader().getResourceAsStream("database/phongtro247_postgresql_database.sql");
                if (is != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        StringBuilder sql = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (!line.isEmpty() && !line.startsWith("--")) {
                                sql.append(line).append(" ");
                                if (line.endsWith(";")) {
                                    String sqlStatement = sql.toString().trim();
                                    if (!sqlStatement.isEmpty()) {
                                        try {
                                            stmt.execute(sqlStatement);
                                            logger.info("Executed: " + sqlStatement.substring(0, Math.min(100, sqlStatement.length())) + "...");
                                        } catch (Exception e) {
                                            logger.warning("Failed to execute: " + sqlStatement + " - " + e.getMessage());
                                        }
                                    }
                                    sql.setLength(0);
                                }
                            }
                        }
                    }
                    logger.info("Database initialization completed successfully!");
                } else {
                    logger.severe("Database initialization script not found!");
                }
            }

        } catch (Exception e) {
            logger.severe("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Database initializer destroyed.");
    }
}