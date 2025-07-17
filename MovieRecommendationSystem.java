// Save this as MovieRecommendationSystem.java
import java.util.*;

import java.util.stream.Collectors;

class Movie {
    private int id;
    private String title;
    private String genre;
    private String director;
    private int year;
    private double rating;
    private String description;
    
    public Movie(int id, String title, String genre, String director, int year, double rating, String description) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.description = description;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getDirector() { return director; }
    public int getYear() { return year; }
    public double getRating() { return rating; }
    public String getDescription() { return description; }
    
    @Override
    public String toString() {
        return String.format("Movie{id=%d, title='%s', genre='%s', director='%s', year=%d, rating=%.1f}", 
                           id, title, genre, director, year, rating);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie movie = (Movie) obj;
        return id == movie.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

class User {
    private int userId;
    private String name;
    private List<String> favoriteGenres;
    private List<Integer> watchedMovies;
    private Map<Integer, Double> movieRatings;
    
    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
        this.favoriteGenres = new ArrayList<>();
        this.watchedMovies = new ArrayList<>();
        this.movieRatings = new HashMap<>();
    }
    
    public void addFavoriteGenre(String genre) {
        if (!favoriteGenres.contains(genre)) {
            favoriteGenres.add(genre);
        }
    }
    
    public void addWatchedMovie(int movieId) {
        if (!watchedMovies.contains(movieId)) {
            watchedMovies.add(movieId);
        }
    }
    
    public void rateMovie(int movieId, double rating) {
        if (rating >= 1.0 && rating <= 5.0) {
            movieRatings.put(movieId, rating);
            addWatchedMovie(movieId);
        }
    }
    
    // Getters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public List<String> getFavoriteGenres() { return new ArrayList<>(favoriteGenres); }
    public List<Integer> getWatchedMovies() { return new ArrayList<>(watchedMovies); }
    public Map<Integer, Double> getMovieRatings() { return new HashMap<>(movieRatings); }
    
    public double getAverageRating() {
        if (movieRatings.isEmpty()) return 0.0;
        return movieRatings.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    @Override
    public String toString() {
        return String.format("User{userId=%d, name='%s', favoriteGenres=%s, watchedMovies=%d}", 
                           userId, name, favoriteGenres, watchedMovies.size());
    }
}

class MovieDatabase {
    private Map<Integer, Movie> movies;
    private Map<String, List<Movie>> genreIndex;
    
    public MovieDatabase() {
        this.movies = new HashMap<>();
        this.genreIndex = new HashMap<>();
        initializeMovies();
    }
    
    private void initializeMovies() {
        // Sample movie data
        addMovie(new Movie(1, "The Shawshank Redemption", "Drama", "Frank Darabont", 1994, 9.3, "Two imprisoned men bond over a number of years"));
        addMovie(new Movie(2, "The Godfather", "Crime", "Francis Ford Coppola", 1972, 9.2, "The aging patriarch of an organized crime dynasty"));
        addMovie(new Movie(3, "The Dark Knight", "Action", "Christopher Nolan", 2008, 9.0, "Batman faces the Joker in Gotham City"));
        addMovie(new Movie(4, "Pulp Fiction", "Crime", "Quentin Tarantino", 1994, 8.9, "The lives of two mob hitmen intertwine"));
        addMovie(new Movie(5, "Forrest Gump", "Drama", "Robert Zemeckis", 1994, 8.8, "The presidencies of Kennedy and Johnson through the eyes of Alabama man"));
        addMovie(new Movie(6, "Inception", "Sci-Fi", "Christopher Nolan", 2010, 8.8, "A thief who steals corporate secrets through dream-sharing technology"));
        addMovie(new Movie(7, "The Matrix", "Sci-Fi", "The Wachowskis", 1999, 8.7, "A computer hacker learns reality is a simulation"));
        addMovie(new Movie(8, "Goodfellas", "Crime", "Martin Scorsese", 1990, 8.7, "The story of Henry Hill and his life in the mob"));
        addMovie(new Movie(9, "The Lord of the Rings: The Return of the King", "Fantasy", "Peter Jackson", 2003, 8.9, "Gandalf and Aragorn lead the World of Men against Sauron's army"));
        addMovie(new Movie(10, "Fight Club", "Drama", "David Fincher", 1999, 8.8, "An insomniac office worker forms an underground fight club"));
        addMovie(new Movie(11, "Star Wars: Episode IV - A New Hope", "Sci-Fi", "George Lucas", 1977, 8.6, "Luke Skywalker joins forces with rebels to save Princess Leia"));
        addMovie(new Movie(12, "The Silence of the Lambs", "Thriller", "Jonathan Demme", 1991, 8.6, "A young FBI cadet must confide in Dr. Hannibal Lecter"));
        addMovie(new Movie(13, "Titanic", "Romance", "James Cameron", 1997, 7.8, "A seventeen-year-old aristocrat falls in love with a poor artist"));
        addMovie(new Movie(14, "Casablanca", "Romance", "Michael Curtiz", 1942, 8.5, "A cynical American expatriate struggles to decide whether to help his former lover"));
        addMovie(new Movie(15, "The Avengers", "Action", "Joss Whedon", 2012, 8.0, "Earth's mightiest heroes must come together to stop an alien invasion"));
    }
    
    public void addMovie(Movie movie) {
        movies.put(movie.getId(), movie);
        genreIndex.computeIfAbsent(movie.getGenre(), k -> new ArrayList<>()).add(movie);
    }
    
    public Movie getMovie(int id) {
        return movies.get(id);
    }
    
    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }
    
    public List<Movie> getMoviesByGenre(String genre) {
        return genreIndex.getOrDefault(genre, new ArrayList<>());
    }
    
    public List<Movie> getMoviesByDirector(String director) {
        return movies.values().stream()
                .filter(movie -> movie.getDirector().equalsIgnoreCase(director))
                .collect(Collectors.toList());
    }
    
    public List<Movie> getMoviesByYearRange(int startYear, int endYear) {
        return movies.values().stream()
                .filter(movie -> movie.getYear() >= startYear && movie.getYear() <= endYear)
                .collect(Collectors.toList());
    }
    
    public List<Movie> getHighRatedMovies(double minRating) {
        return movies.values().stream()
                .filter(movie -> movie.getRating() >= minRating)
                .sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()))
                .collect(Collectors.toList());
    }
    
    public List<String> getAllGenres() {
        return new ArrayList<>(genreIndex.keySet());
    }
}

class RecommendationEngine {
    private MovieDatabase movieDatabase;
    private Map<Integer, User> users;
    
    public RecommendationEngine(MovieDatabase movieDatabase) {
        this.movieDatabase = movieDatabase;
        this.users = new HashMap<>();
    }
    
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }
    
    public List<Movie> getRecommendations(int userId, int numRecommendations) {
        User user = users.get(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        
        Set<Movie> recommendations = new HashSet<>();
        
        // Content-based filtering
        recommendations.addAll(getContentBasedRecommendations(user, numRecommendations));
        
        // Collaborative filtering
        recommendations.addAll(getCollaborativeRecommendations(user, numRecommendations));
        
        // Popularity-based recommendations
        recommendations.addAll(getPopularityBasedRecommendations(user, numRecommendations));
        
        // Remove already watched movies
        recommendations.removeIf(movie -> user.getWatchedMovies().contains(movie.getId()));
        
        // Sort by rating and return top recommendations
        return recommendations.stream()
                .sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()))
                .limit(numRecommendations)
                .collect(Collectors.toList());
    }
    
    private List<Movie> getContentBasedRecommendations(User user, int numRecommendations) {
        List<Movie> recommendations = new ArrayList<>();
        
        // Recommend based on favorite genres
        for (String genre : user.getFavoriteGenres()) {
            List<Movie> genreMovies = movieDatabase.getMoviesByGenre(genre);
            genreMovies.removeIf(movie -> user.getWatchedMovies().contains(movie.getId()));
            
            genreMovies.stream()
                    .sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()))
                    .limit(numRecommendations / Math.max(1, user.getFavoriteGenres().size()))
                    .forEach(recommendations::add);
        }
        
        // Recommend based on directors of highly rated movies
        Map<Integer, Double> userRatings = user.getMovieRatings();
        Set<String> preferredDirectors = new HashSet<>();
        
        for (Map.Entry<Integer, Double> entry : userRatings.entrySet()) {
            if (entry.getValue() >= 4.0) {
                Movie movie = movieDatabase.getMovie(entry.getKey());
                if (movie != null) {
                    preferredDirectors.add(movie.getDirector());
                }
            }
        }
        
        for (String director : preferredDirectors) {
            List<Movie> directorMovies = movieDatabase.getMoviesByDirector(director);
            directorMovies.removeIf(movie -> user.getWatchedMovies().contains(movie.getId()));
            
            directorMovies.stream()
                    .sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()))
                    .limit(2)
                    .forEach(recommendations::add);
        }
        
        return recommendations;
    }
    
    private List<Movie> getCollaborativeRecommendations(User user, int numRecommendations) {
        List<Movie> recommendations = new ArrayList<>();
        
        // Find similar users based on movie ratings
        List<User> similarUsers = findSimilarUsers(user, 5);
        
        for (User similarUser : similarUsers) {
            Map<Integer, Double> similarUserRatings = similarUser.getMovieRatings();
            
            for (Map.Entry<Integer, Double> entry : similarUserRatings.entrySet()) {
                if (entry.getValue() >= 4.0 && !user.getWatchedMovies().contains(entry.getKey())) {
                    Movie movie = movieDatabase.getMovie(entry.getKey());
                    if (movie != null) {
                        recommendations.add(movie);
                    }
                }
            }
        }
        
        return recommendations.stream()
                .distinct()
                .sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()))
                .limit(numRecommendations)
                .collect(Collectors.toList());
    }
    
    private List<Movie> getPopularityBasedRecommendations(User user, int numRecommendations) {
        return movieDatabase.getHighRatedMovies(8.0).stream()
                .filter(movie -> !user.getWatchedMovies().contains(movie.getId()))
                .limit(numRecommendations)
                .collect(Collectors.toList());
    }
    
    private List<User> findSimilarUsers(User user, int numSimilar) {
        List<UserSimilarity> similarities = new ArrayList<>();
        
        for (User otherUser : users.values()) {
            if (otherUser.getUserId() != user.getUserId()) {
                double similarity = calculateUserSimilarity(user, otherUser);
                if (similarity > 0) {
                    similarities.add(new UserSimilarity(otherUser, similarity));
                }
            }
        }
        
        return similarities.stream()
                .sorted((s1, s2) -> Double.compare(s2.similarity, s1.similarity))
                .limit(numSimilar)
                .map(s -> s.user)
                .collect(Collectors.toList());
    }
    
    private double calculateUserSimilarity(User user1, User user2) {
        Map<Integer, Double> ratings1 = user1.getMovieRatings();
        Map<Integer, Double> ratings2 = user2.getMovieRatings();
        
        Set<Integer> commonMovies = new HashSet<>(ratings1.keySet());
        commonMovies.retainAll(ratings2.keySet());
        
        if (commonMovies.isEmpty()) return 0.0;
        
        // Calculate cosine similarity
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (Integer movieId : commonMovies) {
            double rating1 = ratings1.get(movieId);
            double rating2 = ratings2.get(movieId);
            
            dotProduct += rating1 * rating2;
            norm1 += rating1 * rating1;
            norm2 += rating2 * rating2;
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) return 0.0;
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    private static class UserSimilarity {
        User user;
        double similarity;
        
        UserSimilarity(User user, double similarity) {
            this.user = user;
            this.similarity = similarity;
        }
    }
    
    public User getUser(int userId) {
        return users.get(userId);
    }
    
    public Collection<User> getAllUsers() {
        return users.values();
    }
}

public class MovieRecommendationSystem {
    private MovieDatabase movieDatabase;
    private RecommendationEngine recommendationEngine;
    private Scanner scanner;
    
    public MovieRecommendationSystem() {
        this.movieDatabase = new MovieDatabase();
        this.recommendationEngine = new RecommendationEngine(movieDatabase);
        this.scanner = new Scanner(System.in);
        initializeSampleUsers();
    }
    
    private void initializeSampleUsers() {
        // Create sample users with preferences and ratings
        User user1 = new User(1, "Alice");
        user1.addFavoriteGenre("Sci-Fi");
        user1.addFavoriteGenre("Action");
        user1.rateMovie(6, 5.0); // Inception
        user1.rateMovie(7, 4.5); // The Matrix
        user1.rateMovie(3, 4.0); // The Dark Knight
        user1.rateMovie(15, 3.5); // The Avengers
        
        User user2 = new User(2, "Bob");
        user2.addFavoriteGenre("Crime");
        user2.addFavoriteGenre("Drama");
        user2.rateMovie(2, 5.0); // The Godfather
        user2.rateMovie(4, 4.5); // Pulp Fiction
        user2.rateMovie(8, 4.0); // Goodfellas
        user2.rateMovie(1, 4.5); // The Shawshank Redemption
        
        User user3 = new User(3, "Charlie");
        user3.addFavoriteGenre("Drama");
        user3.addFavoriteGenre("Romance");
        user3.rateMovie(1, 5.0); // The Shawshank Redemption
        user3.rateMovie(5, 4.0); // Forrest Gump
        user3.rateMovie(13, 3.5); // Titanic
        user3.rateMovie(14, 4.0); // Casablanca
        
        recommendationEngine.addUser(user1);
        recommendationEngine.addUser(user2);
        recommendationEngine.addUser(user3);
    }
    
    public void run() {
        System.out.println("=== Movie Recommendation System ===");
        
        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    viewMovies();
                    break;
                case 3:
                    rateMovie();
                    break;
                case 4:
                    addFavoriteGenre();
                    break;
                case 5:
                    getRecommendations();
                    break;
                case 6:
                    viewUserProfile();
                    break;
                case 7:
                    searchMovies();
                    break;
                case 8:
                    System.out.println("Thank you for using the Movie Recommendation System!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void displayMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Create New User");
        System.out.println("2. View All Movies");
        System.out.println("3. Rate a Movie");
        System.out.println("4. Add Favorite Genre");
        System.out.println("5. Get Recommendations");
        System.out.println("6. View User Profile");
        System.out.println("7. Search Movies");
        System.out.println("8. Exit");
    }
    
    private void createUser() {
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();
        
        int userId = recommendationEngine.getAllUsers().size() + 1;
        User user = new User(userId, name);
        recommendationEngine.addUser(user);
        
        System.out.println("User created successfully! User ID: " + userId);
    }
    
    private void viewMovies() {
        System.out.println("\n=== Available Movies ===");
        List<Movie> movies = movieDatabase.getAllMovies();
        
        for (Movie movie : movies) {
            System.out.printf("ID: %d | %s (%d) | Genre: %s | Rating: %.1f\n",
                    movie.getId(), movie.getTitle(), movie.getYear(), movie.getGenre(), movie.getRating());
        }
    }
    
    private void rateMovie() {
        int userId = getIntInput("Enter your user ID: ");
        User user = recommendationEngine.getUser(userId);
        
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        
        int movieId = getIntInput("Enter movie ID to rate: ");
        Movie movie = movieDatabase.getMovie(movieId);
        
        if (movie == null) {
            System.out.println("Movie not found!");
            return;
        }
        
        System.out.println("Rating movie: " + movie.getTitle());
        double rating = getDoubleInput("Enter rating (1.0 - 5.0): ");
        
        if (rating < 1.0 || rating > 5.0) {
            System.out.println("Invalid rating! Please enter a value between 1.0 and 5.0");
            return;
        }
        
        user.rateMovie(movieId, rating);
        System.out.println("Rating added successfully!");
    }
    
    private void addFavoriteGenre() {
        int userId = getIntInput("Enter your user ID: ");
        User user = recommendationEngine.getUser(userId);
        
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        
        System.out.println("Available genres: " + movieDatabase.getAllGenres());
        System.out.print("Enter favorite genre: ");
        String genre = scanner.nextLine();
        
        user.addFavoriteGenre(genre);
        System.out.println("Favorite genre added successfully!");
    }
    
    private void getRecommendations() {
        int userId = getIntInput("Enter your user ID: ");
        User user = recommendationEngine.getUser(userId);
        
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        
        int numRecommendations = getIntInput("Enter number of recommendations (default 5): ");
        if (numRecommendations <= 0) numRecommendations = 5;
        
        List<Movie> recommendations = recommendationEngine.getRecommendations(userId, numRecommendations);
        
        System.out.println("\n=== Recommendations for " + user.getName() + " ===");
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available. Try rating some movies or adding favorite genres!");
        } else {
            for (int i = 0; i < recommendations.size(); i++) {
                Movie movie = recommendations.get(i);
                System.out.printf("%d. %s (%d) | Genre: %s | Rating: %.1f\n",
                        i + 1, movie.getTitle(), movie.getYear(), movie.getGenre(), movie.getRating());
                System.out.println("   " + movie.getDescription());
            }
        }
    }
    
    private void viewUserProfile() {
        int userId = getIntInput("Enter user ID: ");
        User user = recommendationEngine.getUser(userId);
        
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        
        System.out.println("\n=== User Profile ===");
        System.out.println("Name: " + user.getName());
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Favorite Genres: " + user.getFavoriteGenres());
        System.out.println("Movies Watched: " + user.getWatchedMovies().size());
        System.out.println("Average Rating: " + String.format("%.1f", user.getAverageRating()));
        
        if (!user.getMovieRatings().isEmpty()) {
            System.out.println("\nRated Movies:");
            for (Map.Entry<Integer, Double> entry : user.getMovieRatings().entrySet()) {
                Movie movie = movieDatabase.getMovie(entry.getKey());
                if (movie != null) {
                    System.out.printf("  %s: %.1f/5.0\n", movie.getTitle(), entry.getValue());
                }
            }
        }
    }
    
    private void searchMovies() {
        System.out.println("\n=== Search Movies ===");
        System.out.println("1. Search by Genre");
        System.out.println("2. Search by Director");
        System.out.println("3. Search by Year Range");
        System.out.println("4. Search High Rated Movies");
        
        int choice = getIntInput("Enter search option: ");
        
        switch (choice) {
            case 1:
                searchByGenre();
                break;
            case 2:
                searchByDirector();
                break;
            case 3:
                searchByYearRange();
                break;
            case 4:
                searchHighRatedMovies();
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }
    
    private void searchByGenre() {
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();
        
        List<Movie> movies = movieDatabase.getMoviesByGenre(genre);
        displayMovieList(movies, "Movies in " + genre + " genre");
    }
    
    private void searchByDirector() {
        System.out.print("Enter director name: ");
        String director = scanner.nextLine();
        
        List<Movie> movies = movieDatabase.getMoviesByDirector(director);
        displayMovieList(movies, "Movies by " + director);
    }
    
    private void searchByYearRange() {
        int startYear = getIntInput("Enter start year: ");
        int endYear = getIntInput("Enter end year: ");
        
        List<Movie> movies = movieDatabase.getMoviesByYearRange(startYear, endYear);
        displayMovieList(movies, "Movies from " + startYear + " to " + endYear);
    }
    
    private void searchHighRatedMovies() {
        double minRating = getDoubleInput("Enter minimum rating: ");
        
        List<Movie> movies = movieDatabase.getHighRatedMovies(minRating);
        displayMovieList(movies, "Movies with rating >= " + minRating);
    }
    
    private void displayMovieList(List<Movie> movies, String title) {
        System.out.println("\n=== " + title + " ===");
        if (movies.isEmpty()) {
            System.out.println("No movies found!");
        } else {
            for (Movie movie : movies) {
                System.out.printf("ID: %d | %s (%d) | Genre: %s | Director: %s | Rating: %.1f\n",
                        movie.getId(), movie.getTitle(), movie.getYear(), movie.getGenre(), movie.getDirector(), movie.getRating());
            }
        }
    }
    
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }
    
    private double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return value;
    }
    
    public static void main(String[] args) {
        MovieRecommendationSystem system = new MovieRecommendationSystem();
        system.run();
    }
}