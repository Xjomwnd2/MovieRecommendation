// MovieRecommendation.java
import config.Config;

public class MovieRecommendation {
    public static void main(String[] args) {
        System.out.println("Starting Movie Recommendation System...");
        
        Config config = Config.getInstance();
        
        String apiKey = config.getTmdbApiKey();
        int maxResults = config.getMaxRecommendations();
        
        System.out.println("API Key: " + apiKey);
        System.out.println("Max Results: " + maxResults);
        System.out.println("System ready!");
    }
}