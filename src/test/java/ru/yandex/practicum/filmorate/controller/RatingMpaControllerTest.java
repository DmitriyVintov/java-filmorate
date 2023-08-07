package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbMpaStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RatingMpaControllerTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private RatingMpaController ratingMpaController;

    public RatingMpaControllerTest(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        ratingMpaController = new RatingMpaController(new MpaService(new DbMpaStorage(jdbcTemplate)));
    }

    @Test
    @DisplayName("Получение списка рейтингов Mpa")
    void getMpaList() {
        assertEquals(5, ratingMpaController.getMpaList().size());
    }

    @Test
    @DisplayName("Получение рейтинга Mpa по id")
    void getMpaById() {
        assertEquals("G", ratingMpaController.getMpaById(1).getName());
    }
}