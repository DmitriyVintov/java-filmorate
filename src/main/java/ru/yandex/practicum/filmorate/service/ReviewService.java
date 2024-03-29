package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.model.UserEventOperation;
import ru.yandex.practicum.filmorate.model.UserEventType;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbReviewStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbUserEventStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final DbReviewStorage dbReviewStorage;
    private final DbUserEventStorage eventStorage;

    public Review addReview(Review review) {
        validateReview(review);
        log.info("Создан отзыв: {}", review);
        Review review1 = dbReviewStorage.create(review);
        eventStorage.create(new UserEvent(review1.getUserId(), UserEventType.REVIEW, UserEventOperation.ADD, review1.getReviewId()));
        return review1;
    }

    public Review getReviewById(Integer reviewId) {
        Review review = dbReviewStorage.getById(reviewId);
        log.info("Получение отзыва с id {}: {}", reviewId, review);
        return review;
    }

    public Review updateReview(Review review) {
        validateReview(review);
        log.info("Обновлен отзыв: {}", review);
        Review review1 = dbReviewStorage.update(review);
        eventStorage.create(new UserEvent(getReviewById(review1.getReviewId()).getUserId(), UserEventType.REVIEW, UserEventOperation.UPDATE, review1.getReviewId()));
        return review1;
    }

    public void deleteReview(Integer reviewId) {
        eventStorage.create(new UserEvent(getReviewById(reviewId).getUserId(), UserEventType.REVIEW, UserEventOperation.REMOVE, reviewId));
        dbReviewStorage.deleteById(reviewId);
        log.info("Отзыв с id {} удален", reviewId);
    }

    public void addLike(Integer userId, Integer reviewId) {
        if (dbReviewStorage.checkUserReview(userId, reviewId) > 0) {
            log.error("Пользователь уже ставил лайк отзыву.");
            throw new ValidationException("Пользователь уже ставил лайк отзыву.");
        }
        log.info("Пользователь id {} поставил лайк отзыву id {}", userId, reviewId);
        dbReviewStorage.addLike(userId, reviewId);
    }

    public void addDislike(Integer userId, Integer reviewId) {
        if (dbReviewStorage.checkUserReview(userId, reviewId) > 0) {
            log.error("Пользователь уже ставил дизлайк отзыву.");
            throw new ValidationException("Пользователь уже ставил дизлайк отзыву.");
        }
        log.info("Пользователь id {} поставил дизлайк отзыву id {}", userId, reviewId);
        dbReviewStorage.addDislike(userId, reviewId);
    }

    public void removeLike(Integer userId, Integer reviewId) {
        log.info("Пользователь id {} удалил лайк у отзыва id {}", userId, reviewId);
        dbReviewStorage.removeLike(userId, reviewId);
    }

    public void removeDislike(Integer userId, Integer reviewId) {
        log.info("Пользователь id {} удалил лайк у отзыва id {}", userId, reviewId);
        dbReviewStorage.removeDislike(userId, reviewId);
    }

    public List<Review> getReviewsByFilmIdWithCount(Integer filmId, Integer count) {
        if (count <= 0) {
            log.error("Передано отрицательное значение count.");
            throw new ValidationException("Значение count не может быть отрицательным");
        }
        log.info(String.format("Получение списка всех отзывов по идентификатору фильма с id %s", filmId));
        return dbReviewStorage.getReviewsByFilmIdWithCount(filmId, count);
    }

    private void validateReview(Review review) {
        if (review.getFilmId() == null) {
            log.error("Фильм равен null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Фильм равен null");
        }
        if (review.getUserId() == null) {
            log.error("Пользователь равен null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь равен null");
        }
        if (review.getIsPositive() == null) {
            log.error("Значение поля 'isPositive' равно null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Значение поля 'isPositive' равно null");
        }
    }
}