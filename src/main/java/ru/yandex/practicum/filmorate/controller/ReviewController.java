package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        log.info("Поступил запрос на создание отзыва.");
        return reviewService.addReview(review);
    }

    @PutMapping()
    public Review updateReview(@RequestBody Review review) {
        log.info(String.format("Поступил запрос на обновление отзыва с id %s", review.getReviewId()));
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на удаление отзыва с id %s", id));
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на получение отзыва с id %s", id));
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsByFilmIdWithCount(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        log.info(String.format("Поступил запрос на получение всех отзывов по идентификатору фильма с id %s", filmId));
        return reviewService.getReviewsByFilmIdWithCount(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Поступил запрос на добавление лайка отзыву с id %s от пользователя с id %s", id, userId));
        reviewService.addLike(userId, id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Поступил запрос на добавление дизлайка отзыву с id %s от пользователя с id %s", id, userId));
        reviewService.addDislike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Поступил запрос на удаление лайка отзыву с id %s от пользователя с id %s", id, userId));
        reviewService.removeLike(userId, id);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Поступил запрос на удаление дизлайка отзыву с id %s от пользователя с id %s", id, userId));
        reviewService.removeDislike(userId, id);
    }
}
