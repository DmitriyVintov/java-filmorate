package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DbReviewStorage implements Storage<Review> {

    private static final String GET_REVIEW = "SELECT * FROM reviews WHERE review_id = ?";

    private static final String GET_ALL_REVIEW = "SELECT * FROM reviews";

    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_Positive=? WHERE review_id = ?";

    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE review_id = ?";

    private static final String ADD_LIKE =
            "INSERT INTO likes_reviews (user_id, review_id, is_Positive) VALUES (?, ?, true)";

    private static final String UPDATE_LIKE_COUNT =
            "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";

    private static final String ADD_DISLIKE =
            "INSERT INTO likes_reviews (user_id, review_id, is_Positive) VALUES (?, ?, false)";

    private static final String REMOVE_LIKE =
            "DELETE FROM likes_reviews WHERE user_id = ? AND review_id = ? AND is_Positive = true";

    private static final String REMOVE_DISLIKE =
            "DELETE FROM likes_reviews WHERE user_id = ? AND review_id = ? AND is_Positive = false";

    private static final String UPDATE_DISLIKE_COUNT =
            "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";

    private static final String GET_REVIEWS_BY_FILM =
            "SELECT * FROM reviews WHERE film_id = COALESCE(?, film_id) ORDER BY useful DESC LIMIT ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        checkUserIdExists(review.getUserId());
        checkFilmIdExists(review.getFilmId());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> params = Map.of(
                "content", review.getContent(),
                "is_Positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "useful", 0);

        Number reviewId = simpleJdbcInsert.executeAndReturnKey(params);
        review.setReviewId(reviewId.intValue());
        return review;
    }

    @Override
    public List<Review> getAll() {
        return jdbcTemplate.query(GET_ALL_REVIEW, this::reviewRowMapper);
    }

    @Override
    public Review getById(Integer reviewId) {
        return jdbcTemplate.query(GET_REVIEW, this::reviewRowMapper, reviewId).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Отзыва с id %s не существует", reviewId)));
    }

    @Override
    public Review update(Review review) {
        getById(review.getReviewId());
        jdbcTemplate.update(
                UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        return getById(review.getReviewId());
    }

    @Override
    public void deleteById(Integer reviewId) {
        getById(reviewId);
        jdbcTemplate.update(DELETE_REVIEW, reviewId);
    }

    public void addLike(Integer userId, Integer reviewId) {
        checkUserIdExists(userId);
        getById(reviewId);

        jdbcTemplate.update(ADD_LIKE, userId, reviewId);
        jdbcTemplate.update(UPDATE_LIKE_COUNT, reviewId);
    }

    public void addDislike(Integer userId, Integer reviewId) {
        getById(reviewId);
        checkUserIdExists(userId);

        jdbcTemplate.update(ADD_DISLIKE, userId, reviewId);
        jdbcTemplate.update(UPDATE_DISLIKE_COUNT, reviewId);
    }

    public void removeLike(Integer userId, Integer reviewId) {
        getById(reviewId);
        checkUserIdExists(userId);

        jdbcTemplate.update(REMOVE_LIKE, userId, reviewId);
        jdbcTemplate.update(UPDATE_DISLIKE_COUNT, reviewId);
    }

    public void removeDislike(Integer userId, Integer reviewId) {
        getById(reviewId);
        checkUserIdExists(userId);

        jdbcTemplate.update(REMOVE_DISLIKE, userId, reviewId);
        jdbcTemplate.update(UPDATE_LIKE_COUNT, reviewId);
    }

    public List<Review> getReviewsByFilmIdWithCount(Integer filmId, Integer count) {
        return jdbcTemplate.query(GET_REVIEWS_BY_FILM, this::reviewRowMapper, filmId, count != null ? count : 10);
    }

    public Review reviewRowMapper(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_Positive"));
        review.setUserId(rs.getInt("user_id"));
        review.setFilmId(rs.getInt("film_id"));
        review.setRating(rs.getInt("useful"));
        return review;
    }

    public void checkUserIdExists(Integer userId) throws NotFoundException {
        String checkUser = "SELECT COUNT(user_id) FROM users WHERE user_id = ?";
        int countUser = jdbcTemplate.queryForObject(checkUser, Integer.class, userId);

        if (countUser <= 0) {
            throw new NotFoundException("Пользователя с таким ID " + userId + " не существует.");
        }
    }

    public void checkFilmIdExists(Integer filmId) throws NotFoundException {
        String checkFilm = "SELECT COUNT(film_id) FROM films WHERE film_id = ?";
        int countFilm = jdbcTemplate.queryForObject(checkFilm, Integer.class, filmId);

        if (countFilm <= 0) {
            throw new NotFoundException("Фильма с таким ID " + filmId + " не существует.");
        }
    }

    public int checkUserReview(Integer userId, Integer reviewId) throws NotFoundException  {
        String sql = "SELECT COUNT(*) FROM likes_reviews WHERE user_id = ? AND review_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, reviewId);
    }
}