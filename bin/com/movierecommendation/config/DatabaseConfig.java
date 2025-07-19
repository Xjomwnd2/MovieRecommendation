package com.movierecommendation.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database configuration and connection management
 */
public class DatabaseConfig {
    private static HikariDataSource dataSource;
    private static final Dotenv dotenv = Dotenv.load();
    
    static {
        try {
            HikariConfig config = new HikariConfig();
            
            // Database connection properties
            String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s",
                dotenv.get("DB_HOST", "localhost"),
                dotenv.get("DB_PORT", "5432"),
                dotenv.get("DB_NAME", "movie_recommendation"));
            
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dotenv.get("DB_USER"));
            config.setPassword(dotenv.get("DB_PASSWORD"));
            config.setDriverClassName("org.postgresql.Driver");
            
            // Connection pool settings
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setLeakDetectionThreshold(60000);
            
            // Performance settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            
            dataSource = new HikariDataSource(config);
            
            System.out.println("✅ Database connection pool initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize database connection pool: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Get a database connection from the pool
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection pool is not initialized");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Get the data source
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Close the connection pool (call this when shutting down the application)
     */
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("✅ Database connection pool closed");
        }
    }
    
    /**
     * Check if the database connection is healthy
     */
    public static boolean isHealthy() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}