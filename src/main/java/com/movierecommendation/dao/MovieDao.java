package com.movierecommendation.dao;

import com.movierecommendation.model.Movie;
import java.util.List;

public interface MovieDAO {
    void addMovie(Movie movie);
    Movie getMovieById(int id);
    List<Movie> getAllMovies();
    void updateMovie(Movie movie);
    void deleteMovie(int id);
}
