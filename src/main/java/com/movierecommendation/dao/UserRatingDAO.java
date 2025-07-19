package com.movierecommendation.dao;

import com.movierecommendation.model.UserRating;
import java.util.List;

public interface UserRatingDAO {
    void addUserRating(UserRating userRating);
    UserRating getUserRating(int userId, int movieId);
    List<UserRating> getUserRatingsByUser(int userId);
    List<UserRating> getUserRatingsByMovie(int movieId);
    void updateUserRating(UserRating userRating);
    void deleteUserRating(int userId, int movieId);
}
