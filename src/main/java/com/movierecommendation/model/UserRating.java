package com.movierecommendation.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * UserRating entity representing a user's rating of a movie
 */
public class UserRating {
    private Long id;
    private Long userId;
    private Long movieId;
    private Integer rating;
    private String review;
    private LocalDateTime createdAt;
    
    // Default constructor
    public UserRating() {}
    
    // Constructor without ID
    public UserRating(Long userId, Long movieId, Integer rating, String review) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.review = review;
    }
    
    // Full constructor
    public UserRating(Long id, Long userId, Long movieId, Integer rating, String review, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.review = review;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRating that = (UserRating) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("UserRating{id=%d, userId=%d, movieId=%d, rating=%d}", 
            id, userId, movieId, rating);
    }
}