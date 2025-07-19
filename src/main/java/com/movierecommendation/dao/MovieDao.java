// dao/MovieDAO.java
package com.movieapp.dao;

import com.movieapp.model.Movie;
import java.util.List;
import java.util.Optional;

public interface MovieDAO {
    // Create
    void save(Movie movie);
    
    // Read
    Optional<Movie> findById(Long id);
    List<Movie> findAll();
    List<Movie> findByTitle(String title);
    List<Movie> findByGenre(String genre);
    List<Movie> findByYear(int year);
    List<Movie> findByRatingRange(double minRating, double maxRating);
    
    // Update
    void update(Movie movie);
    
    // Delete
    void deleteById(Long id);
    boolean existsById(Long id);
    
    // Statistics
    long count();
    double getAverageRating(Long movieId);
}