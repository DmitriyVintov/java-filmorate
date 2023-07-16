package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> get() {
        return jdbcTemplate.query("select * from mpa_ratings", (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name")
        ));
    }

    public Mpa getById(Integer id) throws NotFoundException {
        List<Mpa> mpaList = jdbcTemplate.query("select * from mpa_ratings where id = ?", (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name")
        ), id);
        if (mpaList.size() != 1) {
            throw new NotFoundException(String.format("Рейтинг MPA с id %s не найден", id));
        }
        return mpaList.get(0);
    }
}
