package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    private final FilmController filmController;
    private final UserController userController;
    private final GenreController genreController;
    private static Validator validator;
    private Film film1;
    private Film film2;
    private Film film3;
    private Film film4;
    private Film film5;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private Genre genre1;
    private Genre genre2;
    private Genre genre3;

    @BeforeAll
    public static void setValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setUp() {
        genre1 = genreController.getGenreById(1);
        genre2 = genreController.getGenreById(2);
        genre3 = genreController.getGenreById(3);
        film1 = new Film("film1", "description1", LocalDate.parse("2011-01-01"),100);
        film1.setMpa(new Mpa(1, "G"));
        film1.setGenres(new HashSet<>(Arrays.asList(genre1, genre2)));
        film2 = new Film("film2", "description2", LocalDate.parse("2012-01-01"),120);
        film2.setMpa(new Mpa(1, "G"));
        film2.setGenres(new HashSet<>(Arrays.asList(genre2, genre3)));
        film3 = new Film("film3", "description2", LocalDate.parse("2012-01-01"),120);
        film3.setMpa(new Mpa(1, "G"));
        film4 = new Film("film4", "description2", LocalDate.parse("2012-01-01"),120);
        film4.setMpa(new Mpa(1, "G"));
        film4.setGenres(new HashSet<>(Collections.singletonList(genre1)));
        film5 = new Film("film5", "description2", LocalDate.parse("2012-01-01"),120);
        film5.setMpa(new Mpa(1, "G"));
        user1 = new User("mail@mail.ru", "login", "name", LocalDate.parse("2011-01-01"));
        user2 = new User("mail2@mail.ru", "login2", "name2", LocalDate.parse("2011-10-01"));
        user3 = new User("mail3@mail.ru", "login3", "name3", LocalDate.parse("2012-09-01"));
        user4 = new User("mail4@mail.ru", "login4", "name4", LocalDate.parse("2012-08-01"));
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
        userController.getUsers();
        filmController.setLike(2, 1);
        filmController.getFilms();
        assertEquals(1, filmController.topFilms(5, 1, 2011).size());
    }

    @Test
    void getCommonFilmsTest() {
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        filmController.createFilm(film4);
        filmController.createFilm(film5);
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        userController.createUser(user4);
        List<Film> commonFilms = filmController.getCommonFilm(1,2);
        Assertions.assertEquals(0,commonFilms.size());
        filmController.setLike(1,1);
        filmController.setLike(1,2);
        filmController.setLike(1,3);
        filmController.setLike(2,1);
        filmController.setLike(2,2);
        filmController.setLike(4,2);
        filmController.setLike(4,3);
        filmController.setLike(3,1);
        filmController.setLike(3,2);

        commonFilms = filmController.getCommonFilm(1,2);

        Assertions.assertEquals(3, commonFilms.size());
    }


}