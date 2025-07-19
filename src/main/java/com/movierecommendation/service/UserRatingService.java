// service/UserRatingService.java
package com.movieapp.service;

import com.movieapp.dao.MovieDAO;
import com.movieapp.dao.UserRatingDAO;
import com.movieapp.model.UserRating;
import java.util.List;
import java.util.Optional;

public class UserRatingService {
    private final UserRatingDAO userRatingDAO;
    private final MovieDAO movieDAO;
    
    public UserRatingService(UserRatingDAO userRatingDAO, MovieDAO movieDAO) {
        this.userRatingDAO = userRatingDAO;
        this.movieDAO = movieDAO;
    }
    
    // Rating CRUD operations
    public void addRating(UserRating rating) {
        validateRating(rating);
        
        // Check if movie exists
        if (!movieDAO.existsById(rating.getMovieId())) {
            throw new IllegalArgumentException("Movie not found");
        }
        
        // Check if user already rated this movie
        if (userRatingDAO.existsByUserIdAndMovieId(rating.getUserId(), rating.getMovieId())) {
            throw new IllegalArgumentException("User has already rated this movie");
        }
        
        userRatingDAO.save(rating);
    }
    
    public void updateRating(UserRating rating) {
        validateRating(rating);
        
        Optional<UserRating> existing = userRatingDAO.findByUserIdAndMovieId(
            rating.getUserId(), rating.getMovieId());
        
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }
        
        rating.setId(existing.get().getId());
        userRatingDAO.update(rating);
    }
    
    public void deleteRating(Long userId, Long movieId) {
        if (!userRatingDAO.existsByUserIdAndMovieId(userId, movieId)) {
            throw new IllegalArgumentException("Rating not found");
        }
        userRatingDAO.deleteByUserIdAndMovieId(userId, movieId);
    }
    
    // Query operations
    public Optional<UserRating> getUserRating(Long userId, Long movieId) {
        return userRatingDAO.findByUserIdAndMovieId(userId, movieId);
    }
    
    public List<UserRating> getUserRatings(Long userId) {
        return userRatingDAO.findByUserId(userId);
    }
    
    public List<UserRating> getMovieRatings(Long movieId) {
        return userRatingDAO.findByMovieId(movieId);
    }
    
    public double getAverageRatingForMovie(Long movieId) {
        return userRatingDAO.getAverageRatingForMovie(movieId);
    }
    
    public List<UserRating> getTopRatedMoviesByUser(Long userId, int limit) {
        return userRatingDAO.getTopRatedMoviesByUser(userId, limit);
    }
    
    // Statistics
    public long getTotalRatingsCount() {
        return userRatingDAO.count();
    }
    
    public long getRatingsCountForMovie(Long movieId) {
        return userRatingDAO.countByMovieId(movieId);
    }
    
    public long getRatingsCountForUser(Long userId) {
        return userRatingDAO.countByUserId(userId);
    }
    
    private void validateRating(UserRating rating) {
        if (rating.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (rating.getMovieId() == null) {
            throw new IllegalArgumentException("Movie ID cannot be null");
        }
        if (rating.getRating() < 1 || rating.getRating() > 10) {
            throw new IllegalArgumentException("Rating must be between 1 and 10");
        }
    }
}