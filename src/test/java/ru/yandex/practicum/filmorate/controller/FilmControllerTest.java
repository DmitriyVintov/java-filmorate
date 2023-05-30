package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static Validator validator;
    private static FilmController controller;
    private static Film film;

    @BeforeAll
    public static void setValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setController() {
        controller = new FilmController();
        film = new Film("film", "descr film", LocalDate.parse("2023-01-01"), 120);
    }

    @Test
    @DisplayName("Создание валидного фильма")
    void shouldBeCreateFilm() {
        controller.createFilm(film);
        assertEquals(1, controller.getFilms().size());
    }

    @Test
    @DisplayName("Проверка валидации фильма, когда название пустое")
    void shouldBeEmptySetWhenNameIsBlank() {
        Film film = new Film("", "descr film", LocalDate.parse("2023-01-01"), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Проверка валидации фильма, когда описание пустое")
    void shouldBeEmptySetWhenDescriptionIsBlank() {
        Film film = new Film("film", "", LocalDate.parse("2023-01-01"), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Проверка валидации фильма, когда дата выхода фильма раньше 28 декабря 1895 года")
    void shouldBeEmptySetWhenDateReleaseEarlier28december1895() {
        Film film = new Film("film", "", LocalDate.parse("1890-01-01"), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Проверка валидации фильма, когда продолжительность фильма не положительная")
    void shouldBeEmptySetWhenDurationIsNegative() {
        Film film = new Film("film", "", LocalDate.parse("2023-01-01"), 0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Обновление валидного фильма")
    void shouldBeUpdatedFilm() {
        controller.createFilm(film);
        Film film1 = new Film("film1", "descr", LocalDate.parse("2022-01-01"), 100);
        film1.setId(1);
        controller.updateFilm(film1);
        assertEquals("film1", controller.getFilms().get(0).getName());
    }

    @Test
    @DisplayName("Получение ошибки при обновлении фильма, если такого не существует")
    void shouldGetExceptionWhenUpdatedFilmWithWrongId() {
        controller.createFilm(film);
        Film film1 = new Film("film1", "descr", LocalDate.parse("2022-01-01"), 100);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.updateFilm(film1));
        assertEquals("Такого фильма не существует", exception.getMessage());
    }

    @Test
    @DisplayName("Получение списка фильма")
    void shouldGetListOfFilms() {
        controller.createFilm(film);
        assertEquals(1, controller.getFilms().size());
    }

    @Test
    @DisplayName("Получение пустого списка фильмов, если еще не создано ни одного фильма")
    void shouldGetEmptyListOfFilms() {
        assertEquals(0, controller.getFilms().size());
    }
}