// DatabaseConnection.java
import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/movie_db";
    private static final String USERNAME = "postgres"; // Change to your PostgreSQL username
    private static final String PASSWORD = "Huruma@2025"; // Change to your PostgreSQL password
    
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver not found", e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    // Initialize database tables
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create movies table
            String createMoviesTable = """
                CREATE TABLE IF NOT EXISTS movies (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    genre VARCHAR(100) NOT NULL,
                    director VARCHAR(255) NOT NULL,
                    year INTEGER NOT NULL,
                    rating DECIMAL(3,1) NOT NULL,
                    description TEXT
                )
            """;
            
            // Create users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // Create user_favorite_genres table
            String createFavoriteGenresTable = """
                CREATE TABLE IF NOT EXISTS user_favorite_genres (
                    id SERIAL PRIMARY KEY,
                    user_id INTEGER REFERENCES users(user_id),
                    genre VARCHAR(100) NOT NULL,
                    UNIQUE(user_id, genre)
                )
            """;
            
            // Create user_movie_ratings table
            String createRatingsTable = """
                CREATE TABLE IF NOT EXISTS user_movie_ratings (
                    id SERIAL PRIMARY KEY,
                    user_id INTEGER REFERENCES users(user_id),
                    movie_id INTEGER REFERENCES movies(id),
                    rating DECIMAL(3,1) NOT NULL,
                    rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE(user_id, movie_id)
                )
            """;
            
            stmt.execute(createMoviesTable);
            stmt.execute(createUsersTable);
            stmt.execute(createFavoriteGenresTable);
            stmt.execute(createRatingsTable);
            
            System.out.println("Database tables created successfully!");
            
            // Insert sample data if tables are empty
            insertSampleData();
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    private static void insertSampleData() {
        try (Connection conn = getConnection()) {
            // Check if movies table is empty
            String checkMovies = "SELECT COUNT(*) FROM movies";
            try (PreparedStatement pstmt = conn.prepareStatement(checkMovies);
                 ResultSet rs = pstmt.executeQuery()) {
                
                if (rs.next() && rs.getInt(1) == 0) {
                    insertSampleMovies(conn);
                    insertSampleUsers(conn);
                    System.out.println("Sample data inserted successfully!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
        }
    }
    
    private static void insertSampleMovies(Connection conn) throws SQLException {
        String insertMovie = "INSERT INTO movies (title, genre, director, year, rating, description) VALUES (?, ?, ?, ?, ?, ?)";
        
        Object[][] movies = {
            {"The Shawshank Redemption", "Drama", "Frank Darabont", 1994, 9.3, "Two imprisoned men bond over a number of years"},
            {"The Godfather", "Crime", "Francis Ford Coppola", 1972, 9.2, "The aging patriarch of an organized crime dynasty"},
            {"The Dark Knight", "Action", "Christopher Nolan", 2008, 9.0, "Batman faces the Joker in Gotham City"},
            {"Pulp Fiction", "Crime", "Quentin Tarantino", 1994, 8.9, "The lives of two mob hitmen intertwine"},
            {"Forrest Gump", "Drama", "Robert Zemeckis", 1994, 8.8, "The presidencies of Kennedy and Johnson through the eyes of Alabama man"},
            {"Inception", "Sci-Fi", "Christopher Nolan", 2010, 8.8, "A thief who steals corporate secrets through dream-sharing technology"},
            {"The Matrix", "Sci-Fi", "The Wachowskis", 1999, 8.7, "A computer hacker learns reality is a simulation"},
            {"Goodfellas", "Crime", "Martin Scorsese", 1990, 8.7, "The story of Henry Hill and his life in the mob"},
            {"The Lord of the Rings: The Return of the King", "Fantasy", "Peter Jackson", 2003, 8.9, "Gandalf and Aragorn lead the World of Men against Sauron's army"},
            {"Fight Club", "Drama", "David Fincher", 1999, 8.8, "An insomniac office worker forms an underground fight club"},
            {"Star Wars: Episode IV - A New Hope", "Sci-Fi", "George Lucas", 1977, 8.6, "Luke Skywalker joins forces with rebels to save Princess Leia"},
            {"The Silence of the Lambs", "Thriller", "Jonathan Demme", 1991, 8.6, "A young FBI cadet must confide in Dr. Hannibal Lecter"},
            {"Titanic", "Romance", "James Cameron", 1997, 7.8, "A seventeen-year-old aristocrat falls in love with a poor artist"},
            {"Casablanca", "Romance", "Michael Curtiz", 1942, 8.5, "A cynical American expatriate struggles to decide whether to help his former lover"},
            {"The Avengers", "Action", "Joss Whedon", 2012, 8.0, "Earth's mightiest heroes must come together to stop an alien invasion"}
        };
        
        try (PreparedStatement pstmt = conn.prepareStatement(insertMovie)) {
            for (Object[] movie : movies) {
                pstmt.setString(1, (String) movie[0]);
                pstmt.setString(2, (String) movie[1]);
                pstmt.setString(3, (String) movie[2]);
                pstmt.setInt(4, (Integer) movie[3]);
                pstmt.setDouble(5, (Double) movie[4]);
                pstmt.setString(6, (String) movie[5]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    private static void insertSampleUsers(Connection conn) throws SQLException {
        String insertUser = "INSERT INTO users (name) VALUES (?)";
        String insertGenre = "INSERT INTO user_favorite_genres (user_id, genre) VALUES (?, ?)";
        String insertRating = "INSERT INTO user_movie_ratings (user_id, movie_id, rating) VALUES (?, ?, ?)";
        
        // Insert users
        try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
            pstmt.setString(1, "Alice");
            pstmt.executeUpdate();
            pstmt.setString(1, "Bob");
            pstmt.executeUpdate();
            pstmt.setString(1, "Charlie");
            pstmt.executeUpdate();
        }
        
        // Insert favorite genres
        try (PreparedStatement pstmt = conn.prepareStatement(insertGenre)) {
            // Alice's genres
            pstmt.setInt(1, 1);
            pstmt.setString(2, "Sci-Fi");
            pstmt.executeUpdate();
            pstmt.setInt(1, 1);
            pstmt.setString(2, "Action");
            pstmt.executeUpdate();
            
            // Bob's genres
            pstmt.setInt(1, 2);
            pstmt.setString(2, "Crime");
            pstmt.executeUpdate();
            pstmt.setInt(1, 2);
            pstmt.setString(2, "Drama");
            pstmt.executeUpdate();
            
            // Charlie's genres
            pstmt.setInt(1, 3);
            pstmt.setString(2, "Drama");
            pstmt.executeUpdate();
            pstmt.setInt(1, 3);
            pstmt.setString(2, "Romance");
            pstmt.executeUpdate();
        }
        
        // Insert ratings
        try (PreparedStatement pstmt = conn.prepareStatement(insertRating)) {
            // Alice's ratings
            pstmt.setInt(1, 1); pstmt.setInt(2, 6); pstmt.setDouble(3, 5.0); pstmt.executeUpdate();
            pstmt.setInt(1, 1); pstmt.setInt(2, 7); pstmt.setDouble(3, 4.5); pstmt.executeUpdate();
            pstmt.setInt(1, 1); pstmt.setInt(2, 3); pstmt.setDouble(3, 4.0); pstmt.executeUpdate();
            pstmt.setInt(1, 1); pstmt.setInt(2, 15); pstmt.setDouble(3, 3.5); pstmt.executeUpdate();
            
            // Bob's ratings
            pstmt.setInt(1, 2); pstmt.setInt(2, 2); pstmt.setDouble(3, 5.0); pstmt.executeUpdate();
            pstmt.setInt(1, 2); pstmt.setInt(2, 4); pstmt.setDouble(3, 4.5); pstmt.executeUpdate();
            pstmt.setInt(1, 2); pstmt.setInt(2, 8); pstmt.setDouble(3, 4.0); pstmt.executeUpdate();
            pstmt.setInt(1, 2); pstmt.setInt(2, 1); pstmt.setDouble(3, 4.5); pstmt.executeUpdate();
            
            // Charlie's ratings
            pstmt.setInt(1, 3); pstmt.setInt(2, 1); pstmt.setDouble(3, 5.0); pstmt.executeUpdate();
            pstmt.setInt(1, 3); pstmt.setInt(2, 5); pstmt.setDouble(3, 4.0); pstmt.executeUpdate();
            pstmt.setInt(1, 3); pstmt.setInt(2, 13); pstmt.setDouble(3, 3.5); pstmt.executeUpdate();
            pstmt.setInt(1, 3); pstmt.setInt(2, 14); pstmt.setDouble(3, 4.0); pstmt.executeUpdate();
        }
    }
}