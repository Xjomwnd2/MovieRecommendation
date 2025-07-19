// dao/UserRatingDAO.java
package com.movieapp.dao;

import com.movieapp.model.UserRating;
import java.util.List;
import java.util.Optional;

public interface UserRatingDAO {
    // Create
    void save(UserRating rating);
    
    // Read
    Optional<UserRating> findById(Long id);
    List<UserRating> findAll();
    List<UserRating> findByUserId(Long userId);
    List<UserRating> findByMovieId(Long movieId);
    Optional<UserRating> findByUserIdAndMovieId(Long userId, Long movieId);
    List<UserRating> findByRatingValue(int rating);
    List<UserRating> findByRatingRange(int minRating, int maxRating);
    
    // Update
    void update(UserRating rating);
    
    // Delete
    void deleteById(Long id);
    void deleteByUserIdAndMovieId(Long userId, Long movieId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    
    // Statistics
    long count();
    long countByMovieId(Long movieId);
    long countByUserId(Long userId);
    double getAverageRatingForMovie(Long movieId);
    List<UserRating> getTopRatedMoviesByUser(Long userId, int limit);
}
