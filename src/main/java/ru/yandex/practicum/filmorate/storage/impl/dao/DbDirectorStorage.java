package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DbDirectorStorage implements Storage<Director> {

    private final JdbcTemplate jdbcTemplate;

    private Director checkDirector(Integer id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        List<Director> checkedDirector = jdbcTemplate.query(sql, (rs, rowNum) -> directorRowMapper(rs), id);
        if (checkedDirector.size() == 0) {
            throw new NotFoundException("Режиссера с ID" + id + "нет в базе!");
        }
        return checkedDirector.get(0);
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> directorRowMapper(rs));
    }

    @Override
    public Director getById(Integer id) throws NotFoundException {
        return checkDirector(id);
    }

    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO directors (director_name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        checkDirector(director.getId());
        String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteById(Integer id) {
        checkDirector(id);
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Director directorRowMapper(ResultSet rs) throws SQLException {
        return new Director(
                rs.getInt("director_id"),
                rs.getString("director_name")
        );
    }
}
