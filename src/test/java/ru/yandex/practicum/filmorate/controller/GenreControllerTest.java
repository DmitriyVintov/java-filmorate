package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbGenreStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreControllerTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private GenreController genreController;

    public GenreControllerTest(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        genreController = new GenreController(new GenreService(new DbGenreStorage(jdbcTemplate)));
    }

    @Test
    @DisplayName("Получение жанров")
    void getGenres() {
        assertEquals(6, genreController.getGenres().size());
    }

    @Test
    @DisplayName("Получение жанра по id")
    void getGenreById() {
        assertEquals("Комедия", genreController.getGenreById(1).getName());
    }
}