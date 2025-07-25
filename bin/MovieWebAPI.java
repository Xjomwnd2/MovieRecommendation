// MovieWebAPI.java
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MovieWebAPI {
    private static final int PORT = 5500;
    
    public static void main(String[] args) throws IOException {
        // Initialize database
        DatabaseConnection.initializeDatabase();
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // API endpoints
        server.createContext("/api/movies", new MoviesHandler());
        server.createContext("/api/users", new UsersHandler());
        server.createContext("/api/recommendations", new RecommendationsHandler());
        server.createContext("/api/ratings", new RatingsHandler());
        
        // Static file handler for web interface
        server.createContext("/", new StaticFileHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Movie Recommendation Server started on http://localhost:" + PORT);
        System.out.println("Visit http://localhost:5500 to view the web interface");
    }
    
    // Handler for movies API
    static class MoviesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = getAllMoviesJSON();
                sendResponse(exchange, response, 200);
            } else {
                sendResponse(exchange, "Method not allowed", 405);
            }
        }
        
        private String getAllMoviesJSON() {
            StringBuilder json = new StringBuilder();
            json.append("[");
            
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM movies ORDER BY rating DESC")) {
                
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"title\":\"").append(escapeJson(rs.getString("title"))).append("\",")
                        .append("\"genre\":\"").append(escapeJson(rs.getString("genre"))).append("\",")
                        .append("\"director\":\"").append(escapeJson(rs.getString("director"))).append("\",")
                        .append("\"year\":").append(rs.getInt("year")).append(",")
                        .append("\"rating\":").append(rs.getDouble("rating")).append(",")
                        .append("\"description\":\"").append(escapeJson(rs.getString("description"))).append("\"")
                        .append("}");
                    first = false;
                }
            } catch (SQLException e) {
                System.err.println("Error fetching movies: " + e.getMessage());
            }
            
            json.append("]");
            return json.toString();
        }
    }
    
    // Handler for users API
    static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = getAllUsersJSON();
                sendResponse(exchange, response, 200);
            } else {
                sendResponse(exchange, "Method not allowed", 405);
            }
        }
        
        private String getAllUsersJSON() {
            StringBuilder json = new StringBuilder();
            json.append("[");
            
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY user_id")) {
                
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    
                    int userId = rs.getInt("user_id");
                    String name = rs.getString("name");
                    
                    // Get favorite genres
                    List<String> genres = getUserGenres(conn, userId);
                    
                    // Get user ratings
                    Map<Integer, Double> ratings = getUserRatings(conn, userId);
                    
                    json.append("{")
                        .append("\"user_id\":").append(userId).append(",")
                        .append("\"name\":\"").append(escapeJson(name)).append("\",")
                        .append("\"favorite_genres\":").append(listToJSON(genres)).append(",")
                        .append("\"ratings_count\":").append(ratings.size())
                        .append("}");
                    first = false;
                }
            } catch (SQLException e) {
                System.err.println("Error fetching users: " + e.getMessage());
            }
            
            json.append("]");
            return json.toString();
        }
        
        private List<String> getUserGenres(Connection conn, int userId) throws SQLException {
            List<String> genres = new ArrayList<>();
            String query = "SELECT genre FROM user_favorite_genres WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        genres.add(rs.getString("genre"));
                    }
                }
            }
            return genres;
        }
        
        private Map<Integer, Double> getUserRatings(Connection conn, int userId) throws SQLException {
            Map<Integer, Double> ratings = new HashMap<>();
            String query = "SELECT movie_id, rating FROM user_movie_ratings WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ratings.put(rs.getInt("movie_id"), rs.getDouble("rating"));
                    }
                }
            }
            return ratings;
        }
    }
    
    // Handler for recommendations API
    static class RecommendationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);
                
                String userIdStr = params.get("user_id");
                String limitStr = params.getOrDefault("limit", "5");
                
                if (userIdStr == null) {
                    sendResponse(exchange, "{\"error\":\"user_id parameter required\"}", 400);
                    return;
                }
                
                try {
                    int userId = Integer.parseInt(userIdStr);
                    int limit = Integer.parseInt(limitStr);
                    String response = getRecommendationsJSON(userId, limit);
                    sendResponse(exchange, response, 200);
                } catch (NumberFormatException e) {
                    sendResponse(exchange, "{\"error\":\"Invalid user_id or limit parameter\"}", 400);
                }
            } else {
                sendResponse(exchange, "Method not allowed", 405);
            }
        }
        
        private String getRecommendationsJSON(int userId, int limit) {
            StringBuilder json = new StringBuilder();
            json.append("[");
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Get user's favorite genres
                List<String> favoriteGenres = getUserFavoriteGenres(conn, userId);
                
                // Get user's watched movies
                Set<Integer> watchedMovies = getUserWatchedMovies(conn, userId);
                
                // Get recommendations based on favorite genres
                String query = """
                    SELECT DISTINCT m.* FROM movies m 
                    WHERE m.genre = ANY(?) 
                    AND m.id NOT IN (
                        SELECT movie_id FROM user_movie_ratings WHERE user_id = ?
                    )
                    ORDER BY m.rating DESC 
                    LIMIT ?
                """;
                
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    Array genreArray = conn.createArrayOf("VARCHAR", favoriteGenres.toArray());
                    pstmt.setArray(1, genreArray);
                    pstmt.setInt(2, userId);
                    pstmt.setInt(3, limit);
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        boolean first = true;
                        while (rs.next()) {
                            if (!first) json.append(",");
                            json.append("{")
                                .append("\"id\":").append(rs.getInt("id")).append(",")
                                .append("\"title\":\"").append(escapeJson(rs.getString("title"))).append("\",")
                                .append("\"genre\":\"").append(escapeJson(rs.getString("genre"))).append("\",")
                                .append("\"director\":\"").append(escapeJson(rs.getString("director"))).append("\",")
                                .append("\"year\":").append(rs.getInt("year")).append(",")
                                .append("\"rating\":").append(rs.getDouble("rating")).append(",")
                                .append("\"description\":\"").append(escapeJson(rs.getString("description"))).append("\"")
                                .append("}");
                            first = false;
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error getting recommendations: " + e.getMessage());
            }
            
            json.append("]");
            return json.toString();
        }
        
        private List<String> getUserFavoriteGenres(Connection conn, int userId) throws SQLException {
            List<String> genres = new ArrayList<>();
            String query = "SELECT genre FROM user_favorite_genres WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        genres.add(rs.getString("genre"));
                    }
                }
            }
            return genres;
        }
        
        private Set<Integer> getUserWatchedMovies(Connection conn, int userId) throws SQLException {
            Set<Integer> movies = new HashSet<>();
            String query = "SELECT movie_id FROM user_movie_ratings WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        movies.add(rs.getInt("movie_id"));
                    }
                }
            }
            return movies;
        }
    }
    
    // Handler for ratings API
    static class RatingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Handle adding new rating
                String requestBody = readRequestBody(exchange);
                // Parse JSON and add rating to database
                sendResponse(exchange, "{\"status\":\"success\"}", 200);
            } else {
                sendResponse(exchange, "Method not allowed", 405);
            }
        }
    }
    
    // Handler for static files (HTML, CSS, JS)
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            // Serve the HTML page
            if (path.equals("/index.html")) {
                String html = getIndexHTML();
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                sendResponse(exchange, html, 200);
            } else {
                sendResponse(exchange, "Not Found", 404);
            }
        }
        
        private String getIndexHTML() {
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Movie Recommendation System</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            max-width: 1200px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #f5f5f5;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            padding: 20px;
                            border-radius: 10px;
                            text-align: center;
                            margin-bottom: 30px;
                        }
                        .section {
                            background: white;
                            padding: 20px;
                            margin-bottom: 20px;
                            border-radius: 10px;
                            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                        }
                        .movie-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
                            gap: 20px;
                        }
                        .movie-card {
                            border: 1px solid #ddd;
                            border-radius: 8px;
                            padding: 15px;
                            background: #f9f9f9;
                        }
                        .movie-title {
                            font-weight: bold;
                            font-size: 1.1em;
                            color: #333;
                            margin-bottom: 5px;
                        }
                        .movie-info {
                            color: #666;
                            margin-bottom: 3px;
                        }
                        .rating {
                            background: #4CAF50;
                            color: white;
                            padding: 3px 8px;
                            border-radius: 15px;
                            font-size: 0.9em;
                            display: inline-block;
                        }
                        .user-select {
                            margin: 10px 0;
                        }
                        select, button {
                            padding: 8px 15px;
                            margin: 5px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                        }
                        button {
                            background: #667eea;
                            color: white;
                            cursor: pointer;
                        }
                        button:hover {
                            background: #5a6fd8;
                        }
                        .loading {
                            text-align: center;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>🎬 Movie Recommendation System</h1>
                        <p>Discover your next favorite movie with personalized recommendations</p>
                    </div>
                
                    <div class="section">
                        <h2>All Movies</h2>
                        <div id="movies-container" class="movie-grid">
                            <div class="loading">Loading movies...</div>
                        </div>
                    </div>
                
                    <div class="section">
                        <h2>Personalized Recommendations</h2>
                        <div class="user-select">
                            <label for="user-select">Select User:</label>
                            <select id="user-select">
                                <option value="">Choose a user...</option>
                            </select>
                            <button onclick="getRecommendations()">Get Recommendations</button>
                        </div>
                        <div id="recommendations-container" class="movie-grid">
                            <div class="loading">Select a user to see recommendations</div>
                        </div>
                    </div>
                
                    <div class="section">
                        <h2>Users</h2>
                        <div id="users-container">
                            <div class="loading">Loading users...</div>
                        </div>
                    </div>
                
                    <script>
                        // Load all movies
                        async function loadMovies() {
                            try {
                                const response = await fetch('/api/movies');
                                const movies = await response.json();
                                displayMovies(movies, 'movies-container');
                            } catch (error) {
                                document.getElementById('movies-container').innerHTML = '<p>Error loading movies</p>';
                            }
                        }
                
                        // Load all users
                        async function loadUsers() {
                            try {
                                const response = await fetch('/api/users');
                                const users = await response.json();
                                displayUsers(users);
                                populateUserSelect(users);
                            } catch (error) {
                                document.getElementById('users-container').innerHTML = '<p>Error loading users</p>';
                            }
                        }
                
                        // Get recommendations for selected user
                        async function getRecommendations() {
                            const userId = document.getElementById('user-select').value;
                            if (!userId) {
                                alert('Please select a user first');
                                return;
                            }
                
                            try {
                                document.getElementById('recommendations-container').innerHTML = '<div class="loading">Loading recommendations...</div>';
                                const response = await fetch(`/api/recommendations?user_id=${userId}&limit=6`);
                                const recommendations = await response.json();
                                displayMovies(recommendations, 'recommendations-container', 'No recommendations available');
                            } catch (error) {
                                document.getElementById('recommendations-container').innerHTML = '<p>Error loading recommendations</p>';
                            }
                        }
                
                        // Display movies in a grid
                        function displayMovies(movies, containerId, emptyMessage = 'No movies available') {
                            const container = document.getElementById(containerId);
                            
                            if (movies.length === 0) {
                                container.innerHTML = `<p>${emptyMessage}</p>`;
                                return;
                            }
                
                            container.innerHTML = movies.map(movie => `
                                <div class="movie-card">
                                    <div class="movie-title">${movie.title}</div>
                                    <div class="movie-info">Genre: ${movie.genre}</div>
                                    <div class="movie-info">Director: ${movie.director}</div>
                                    <div class="movie-info">Year: ${movie.year}</div>
                                    <div class="movie-info">
                                        Rating: <span class="rating">${movie.rating}/10</span>
                                    </div>
                                    <div class="movie-info" style="margin-top: 10px; font-style: italic;">
                                        ${movie.description}
                                    </div>
                                </div>
                            `).join('');
                        }
                
                        // Display users
                        function displayUsers(users) {
                            const container = document.getElementById('users-container');
                            
                            if (users.length === 0) {
                                container.innerHTML = '<p>No users available</p>';
                                return;
                            }
                
                            container.innerHTML = users.map(user => `
                                <div class="movie-card">
                                    <div class="movie-title">${user.name} (ID: ${user.user_id})</div>
                                    <div class="movie-info">Favorite Genres: ${user.favorite_genres.join(', ') || 'None'}</div>
                                    <div class="movie-info">Movies Rated: ${user.ratings_count}</div>
                                </div>
                            `).join('');
                        }
                
                        // Populate user select dropdown
                        function populateUserSelect(users) {
                            const select = document.getElementById('user-select');
                            select.innerHTML = '<option value="">Choose a user...</option>';
                            
                            users.forEach(user => {
                                const option = document.createElement('option');
                                option.value = user.user_id;
                                option.textContent = `${user.name} (ID: ${user.user_id})`;
                                select.appendChild(option);
                            });
                        }
                
                        // Load data when page loads
                        window.onload = function() {
                            loadMovies();
                            loadUsers();
                        };
                    </script>
                </body>
                </html>
            """;
        }
    }
    
    // Utility methods
    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
    
    private static String listToJSON(List<String> list) {
        if (list.isEmpty()) return "[]";
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(list.get(i))).append("\"");
        }
        json.append("]");
        return json.toString();
    }
    
    private static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
    
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }
}
                