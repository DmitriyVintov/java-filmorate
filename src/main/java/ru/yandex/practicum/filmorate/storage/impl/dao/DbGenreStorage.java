package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> get() {
        return jdbcTemplate.query("select * from genre", (rs, rowNum) -> new Genre(
                rs.getInt("id"),
                rs.getString("name")
        ));
    }

    @Override
    public Genre getById(Integer id) throws NotFoundException {
        List<Genre> genres = jdbcTemplate.query("select * from genre where id = ?", (rs, rowNum) -> new Genre(
                rs.getInt("id"),
                rs.getString("name")
        ), id);
        if (genres.size() != 1) {
            throw new NotFoundException(String.format("Жанр с id %s не найден", id));
        }
        return genres.get(0);
    }
}