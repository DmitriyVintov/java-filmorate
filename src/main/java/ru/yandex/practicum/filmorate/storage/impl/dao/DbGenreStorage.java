package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbGenreStorage implements Storage<Genre> {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_ALL_GENRES = "select * from genres order by genre_id";
    private static final String GET_GENRE = "select * from genres where genre_id = ?";

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query(GET_ALL_GENRES, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        ));
    }

    @Override
    public Genre getById(Integer id) throws NotFoundException {
        List<Genre> genres = jdbcTemplate.query(GET_GENRE, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        ), id);
        if (genres.size() != 1) {
            throw new NotFoundException(String.format("Жанр с id %s не найден", id));
        }
        return genres.get(0);
    }

    @Override
    public Genre create(Genre genre) {
        return null;
    }

    @Override
    public Genre update(Genre genre) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }
}