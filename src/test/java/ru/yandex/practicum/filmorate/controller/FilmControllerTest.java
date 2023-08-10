package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private FilmController filmController;
    private UserController userController;
    private static Validator validator;
    private Film film1;
    private Film film2;
    private Film film3;
    private User user1;

    FilmControllerTest(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeAll
    public static void setValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setUp() {
        filmController = new FilmController(new FilmService(new DbFilmStorage(jdbcTemplate), new DbUserStorage(jdbcTemplate)));
        userController = new UserController(new UserService(new DbUserStorage(jdbcTemplate)));
        film1 = new Film("film1", "description1", LocalDate.parse("2011-01-01"),100);
        film1.setMpa(new Mpa(1, "G"));
        film2 = new Film("film2", "description2", LocalDate.parse("2012-01-01"),120);
        film2.setMpa(new Mpa(1, "G"));
        film3 = new Film("film2", "description2", LocalDate.parse("2012-01-01"),120);
        film3.setMpa(new Mpa(1, "G"));
        film3.setGenres(Set.of(new Genre(null, "genre1"), new Genre(2, "comedy")));
        user1 = new User("mail@mail.ru", "login", "name", LocalDate.parse("2011-01-01"));
    }

    @Test
    @DisplayName("Создание валидного фильма")
    void createFilm() {
        filmController.createFilm(film1);
        assertEquals(film1, filmController.getFilmById(1));
    }

    @Test
    @DisplayName("Получение всех фильмов")
    void getFilms() {
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        assertEquals(2, filmController.getFilms().size());
    }

    @Test
    @DisplayName("Получение валидного фильма по id")
    void getFilmById() {
        filmController.createFilm(film1);
        assertEquals(film1, filmController.getFilmById(1));
    }

    @Test
    @DisplayName("Обновление валидного фильма")
    void updateFilm() {
        filmController.createFilm(film1);
        film2.setId(1);
        filmController.updateFilm(film2);
        assertEquals(film2, filmController.getFilmById(1));
    }

    @Test
    @DisplayName("Удаление фильма")
    void deleteFilmById() {
        filmController.createFilm(film1);
        assertEquals(film1, filmController.getFilmById(1));
        filmController.deleteFilmById(1);
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    @DisplayName("Добавление лайка фильму")
    void setLike() {
        Film film = filmController.createFilm(film1);
        User user = userController.createUser(user1);
        filmController.setLike(film.getId(), user.getId());
        assertEquals(1, filmController.getFilmById(film.getId()).getLikes().size());
    }

    @Test
    @DisplayName("Удаление лайка у фильма")
    void removeLike() {
        filmController.createFilm(film1);
        userController.createUser(user1);
        filmController.setLike(1, 1);
        assertEquals(1, filmController.getFilmById(1).getLikes().size());
        filmController.removeLike(1, 1);
        assertEquals(0, filmController.getFilmById(1).getLikes().size());
    }

    @Test
    @DisplayName("Получение списка самых популярных фильмов")
    void getPopularFilms() {
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        userController.createUser(user1);
        filmController.setLike(2, 1);
        assertEquals(2, filmController.getPopularFilms(5).size());
        assertEquals(2, filmController.getPopularFilms(5).get(0).getId());
    }

    @Test
    void getExceptionIfNoValidGenre() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film3);
        assertFalse(violations.isEmpty());
    }
}