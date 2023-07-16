package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public List<Film> get() {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public Film getById(Integer id) {
//        return jdbcTemplate.queryForObject(" select f.name, f.description, f.release_date, f.duration_minutes, rm.name " +
//                "from film f join rating_mpa rm on f.rating_mpa_id = rm.id where id = ?", filmRowMapper(), id);
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void setLike(Integer filmId, Integer userId) {
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        return null;
    }

//    private static RowMapper<Film> filmRowMapper() {
//        return (rs, rowNum) -> new Film(
//                rs.getString("f.name"),
//                rs.getString("description"),
//                LocalDate.parse(rs.getString("release_date")),
//                rs.getInt("duratioin_minutes"),
//                rs.getInt("rm.name")
//        );
//    }
}
