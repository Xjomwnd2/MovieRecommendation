package com.movierecommendation.service;

import com.movierecommendation.dao.UserRatingDAO;
import com.movierecommendation.model.UserRating;
import java.util.List;

public class UserRatingService {
    private final UserRatingDAO userRatingDAO;

    public UserRatingService(UserRatingDAO userRatingDAO) {
        this.userRatingDAO = userRatingDAO;
    }

    public void addRating(UserRating userRating) {
        userRatingDAO.addUserRating(userRating);
    }

    public UserRating getRating(int userId, int movieId) {
        return userRatingDAO.getUserRating(userId, movieId);
    }

    public List<UserRating> getRatingsByUser(int userId) {
        return userRatingDAO.getUserRatingsByUser(userId);
    }

    public List<UserRating> getRatingsByMovie(int movieId) {
        return userRatingDAO.getUserRatingsByMovie(movieId);
    }

    public void updateRating(UserRating userRating) {
        userRatingDAO.updateUserRating(userRating);
    }

    public void deleteRating(int userId, int movieId) {
        userRatingDAO.deleteUserRating(userId, movieId);
    }
}
