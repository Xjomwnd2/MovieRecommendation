package config;

public class DatabaseConfig {
    private static final Config config = Config.getInstance();
    
    public static String getDriver() {
        return config.getProperty("database.driver", "org.sqlite.JDBC");
    }
    
    public static String getUrl() {
        return config.getProperty("database.url", "jdbc:sqlite:data/movies.db");
    }
    
    public static String getUsername() {
        return config.getProperty("database.username", "");
    }
    
    public static String getPassword() {
        return config.getProperty("database.password", "");
    }
}