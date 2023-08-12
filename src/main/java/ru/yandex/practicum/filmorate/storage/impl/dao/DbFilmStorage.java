package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private static final String GET_FILM = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
            "f.rate, f.mpa_id, mr.mpa_name " +
            "FROM films f " +
            "LEFT JOIN mpa_ratings mr on mr.mpa_id = f.mpa_id " +
            "WHERE f.film_id = ?";
    private static final String GET_ALL_FILMS = "SELECT f.film_id, f.film_name, f.description, f.release_date, " +
            "f.duration, f.rate, f.mpa_id, mr.mpa_name " +
            "FROM films f " +
            "LEFT JOIN mpa_ratings mr on mr.mpa_id = f.mpa_id ORDER BY f.film_id";
    private static final String UPDATE_FILM = "UPDATE FILMS SET film_name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ?, rate = ? WHERE film_id = ?";
    private static final String DELETE_FILM = "DELETE FROM FILMS WHERE film_id = ?";
    private static final String GET_GENRES = "SELECT fg.genre_id, g.genre_name FROM films_genres fg " +
            "left join GENRES g on fg.genre_id = g.genre_id where fg.film_id = ?";
    private static final String SET_GENRES = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String GET_LIKES = "SELECT * FROM likes where film_id = ?";
    private static final String SET_LIKE = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? and user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Map<String, Object> params = Map.of(
                "film_name", film.getName(),
                "description", film.getDescription(),
                "release_date", Date.valueOf(film.getReleaseDate()),
                "duration", film.getDuration(),
                "mpa_id", film.getMpa().getId(),
                "rate", film.getRate());
        Number number = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId(number.intValue());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(SET_GENRES, film.getId(), genre.getId());
            }
        }
        return getById(film.getId());
    }

    @Override
    public Film getById(Integer id) {
        return jdbcTemplate.query(GET_FILM, filmRowMapper(), id).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Фильма с id %s не существует", id)));
    }

    @Override
    public List<Film> getAll() {
        List<Film> films;
            films = jdbcTemplate.query(GET_ALL_FILMS, filmRowMapper());
        return films;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate(),
                film.getId());
        jdbcTemplate.update(DELETE_GENRES, film.getId());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(SET_GENRES, film.getId(), genre.getId());
            }
        }
        return getById(film.getId());
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update(DELETE_FILM, id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        Set<Integer> likesFilmById = getLikesFilmById(filmId);
        if (!likesFilmById.contains(userId)) {
            jdbcTemplate.update(SET_LIKE, filmId, userId);
        }
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        Set<Integer> likesFilmById = getLikesFilmById(filmId);
        if (likesFilmById.contains(userId)) {
            jdbcTemplate.update(DELETE_LIKE, filmId, userId);
        }
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film(
                    rs.getString("film_name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration")
            );
            film.setId(rs.getInt("film_id"));
            film.setRate(rs.getInt("rate"));
            film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            film.setLikes(getLikesFilmById(film.getId()));
            film.setGenres(getGenresFilmById(film.getId()));
            return film;
        };
    }

    private Set<Integer> getLikesFilmById(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query(GET_LIKES, (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }

    private Set<Genre> getGenresFilmById(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query(GET_GENRES, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"), rs.getString("genre_name")), filmId));
    }
}