package ru.yandex.practicum.filmorate.service;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
//import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//import java.time.LocalDate;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
class FilmServiceTest {
//    private static Validator validator;
//    private static FilmService filmService;
//    private static UserService userService;
//    private static Film film1;
//    private static Film film2;
//    private static Film film3;
//
//    @BeforeAll
//    public static void setValidator() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    @BeforeEach
//    public void setController() {
//        filmService = new FilmService(new InMemoryFilmStorage());
//        userService = new UserService(new InMemoryUserStorage());
//        film1 = new Film("film", "descr film", LocalDate.parse("2023-01-01"), 120);
//        film2 = new Film("film2", "descr film", LocalDate.parse("2023-01-01"), 120);
//        film3 = new Film("film3", "descr film", LocalDate.parse("2023-01-01"), 120);
//    }
//
//    @Test
//    @DisplayName("Создание валидного фильма")
//    void shouldBeCreateFilm() {
//        filmService.createFilm(film1);
//        assertEquals(1, filmService.getFilms().size());
//    }
//
//    @Test
//    @DisplayName("Проверка валидации фильма, когда название пустое")
//    void shouldBeEmptySetWhenNameIsBlank() {
//        Film film = new Film("", "descr film", LocalDate.parse("2023-01-01"), 120);
//        Set<ConstraintViolation<Film>> violations = validator.validate(film);
//        assertFalse(violations.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Проверка валидации фильма, когда описание пустое")
//    void shouldBeEmptySetWhenDescriptionIsBlank() {
//        Film film = new Film("film", "", LocalDate.parse("2023-01-01"), 120);
//        Set<ConstraintViolation<Film>> violations = validator.validate(film);
//        assertFalse(violations.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Проверка валидации фильма, когда дата выхода фильма раньше 28 декабря 1895 года")
//    void shouldBeEmptySetWhenDateReleaseEarlier28december1895() {
//        Film film = new Film("film", "", LocalDate.parse("1890-01-01"), 120);
//        Set<ConstraintViolation<Film>> violations = validator.validate(film);
//        assertFalse(violations.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Проверка валидации фильма, когда продолжительность фильма не положительная")
//    void shouldBeEmptySetWhenDurationIsNegative() {
//        Film film = new Film("film", "", LocalDate.parse("2023-01-01"), 0);
//        Set<ConstraintViolation<Film>> violations = validator.validate(film);
//        assertFalse(violations.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Обновление валидного фильма")
//    void shouldBeUpdatedFilm() {
//        filmService.createFilm(film1);
//        Film film1 = new Film("film1", "descr", LocalDate.parse("2022-01-01"), 100);
//        film1.setId(1);
//        filmService.updateFilm(film1);
//        assertEquals("film1", filmService.getFilms().get(0).getName());
//    }
//
//    @Test
//    @DisplayName("Получение фильма по id")
//    void shouldGetFilmById() {
//        Film film = filmService.createFilm(film1);
//        assertEquals(film, filmService.getFilmById(film.getId()));
//    }
//
//    @Test
//    @DisplayName("Получение списка фильмов")
//    void shouldGetListOfFilms() {
//        filmService.createFilm(film1);
//        assertEquals(1, filmService.getFilms().size());
//    }
//
//    @Test
//    @DisplayName("Получение пустого списка фильмов, если еще не создано ни одного фильма")
//    void shouldGetEmptyListOfFilms() {
//        assertEquals(0, filmService.getFilms().size());
//    }
//
//    @Test
//    @DisplayName("Удаление фильма по id")
//    void shouldBeDeletedFilm() {
//        filmService.createFilm(film1);
//        assertEquals(1, filmService.getFilms().size());
//        filmService.deleteFilmById(1);
//        assertEquals(0, filmService.getFilms().size());
//    }
//
//    @Test
//    @DisplayName("Добавление лайка фильму")
//    void shouldBeLikedFilm() {
//        filmService.createFilm(film1);
//        assertEquals(0, filmService.getFilmById(1).getLikes().size());
//        userService.createUser(new User("mail@yandex.ru", "login", LocalDate.parse("1985-06-06")));
//        filmService.setLike(1, 1);
//        assertEquals(1, filmService.getFilmById(1).getLikes().size());
//    }
//
//    @Test
//    @DisplayName("Удаление лайка у фильма")
//    void shouldBeRemovedLikeFromFilm() {
//        filmService.createFilm(film1);
//        assertEquals(0, filmService.getFilmById(1).getLikes().size());
//        userService.createUser(new User("mail@yandex.ru", "login", LocalDate.parse("1985-06-06")));
//        filmService.setLike(1, 1);
//        assertEquals(1, filmService.getFilmById(1).getLikes().size());
//        filmService.removeLike(1, 1);
//        assertEquals(0, filmService.getFilmById(1).getLikes().size());
//    }
//
//    @Test
//    @DisplayName("Получение списка самых популярных фильмов")
//    void shouldGetMostPopularFilms() {
//        filmService.createFilm(FilmServiceTest.film1);
//        filmService.createFilm(FilmServiceTest.film2);
//        filmService.createFilm(FilmServiceTest.film3);
//        filmService.setLike(1, 1);
//        filmService.setLike(2, 1);
//        filmService.setLike(2, 2);
//        filmService.setLike(3, 1);
//        filmService.setLike(3, 2);
//        filmService.setLike(3, 3);
//        System.out.println(filmService.getMostPopularFilms(10));
//        assertEquals(3, filmService.getMostPopularFilms(3).size());
//        assertEquals(3, filmService.getMostPopularFilms(3).get(0).getId());
//    }
}