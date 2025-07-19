package com.movierecommendation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Movie entity representing a movie in the database
 */
public class Movie {
    private Long id;
    private String title;
    private String genre;
    private Integer releaseYear;
    private String director;
    private BigDecimal rating;
    private String description;
    private String posterUrl;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Movie() {}
    
    // Constructor without ID (for new movies)
    public Movie(String title, String genre, Integer releaseYear, String director, 
                 BigDecimal rating, String description, String posterUrl) {
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.director = director;
        this.rating = rating;
        this.description = description;
        this.posterUrl = posterUrl;
    }
    
    // Full constructor
    public Movie(Long id, String title, String genre, Integer releaseYear, String director,
                 BigDecimal rating, String description, String posterUrl, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.director = director;
        this.rating = rating;
        this.description = description;
        this.posterUrl = posterUrl;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Movie{id=%d, title='%s', genre='%s', year=%d, rating=%.1f}", 
            id, title, genre, releaseYear, rating);
    }
}