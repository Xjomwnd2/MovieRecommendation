package config;

import java.io.*;
import java.util.Properties;

public class Config {
    private static Config instance;
    private Properties properties;
    
    private Config() {
        loadProperties();
    }
    
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        try {
            // Load from config file
            InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/application.properties");
            if (input != null) {
                properties.load(input);
            }
            
            // Override with environment variables
            loadEnvironmentVariables();
            
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }
    
    private void loadEnvironmentVariables() {
        String apiKey = System.getenv("TMDB_API_KEY");
        if (apiKey != null) {
            properties.setProperty("api.tmdb.api-key", apiKey);
        }
        
        String debug = System.getenv("DEBUG");
        if (debug != null) {
            properties.setProperty("app.debug", debug);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    // Convenient getters for common properties
    public String getTmdbApiKey() {
        return getProperty("api.tmdb.api-key");
    }
    
    public String getTmdbBaseUrl() {
        return getProperty("api.tmdb.base-url");
    }
    
    public int getMaxRecommendations() {
        return getIntProperty("recommendations.max-results", 10);
    }
    
    public double getMinRating() {
        try {
            return Double.parseDouble(getProperty("recommendations.min-rating", "6.0"));
        } catch (NumberFormatException e) {
            return 6.0;
        }
    }
}