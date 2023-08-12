package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DbMpaStorage implements Storage<Mpa> {
    private static final String GET_MPA = "select * from mpa_ratings where mpa_id = ?";
    private static final String GET_MPA_LIST = "select * from mpa_ratings order by mpa_id";
    private static final String UPDATE_MPA = "update mpa_ratings set mpa_name = ? where mpa_id = ?";
    private static final String DELETE_MPA = "delete from mpa_ratings where mpa_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query(GET_MPA_LIST, (rs, rowNum) -> new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("mpa_name")
        ));
    }

    @Override
    public Mpa getById(Integer id) throws NotFoundException {
        List<Mpa> mpaList = jdbcTemplate.query(GET_MPA, (rs, rowNum) -> new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("mpa_name")
        ), id);
        if (mpaList.size() != 1) {
            throw new NotFoundException(String.format("Рейтинг MPA с id %s не найден", id));
        }
        return mpaList.get(0);
    }

    @Override
    public Mpa create(Mpa mpa) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("mpa_ratings")
                .usingGeneratedKeyColumns("mpa_id");
        Map<String, Object> params = Map.of(
                "mpa_name", mpa.getName());
        Number number = simpleJdbcInsert.executeAndReturnKey(params);
        mpa.setId(number.intValue());
        return getById(mpa.getId());
    }

    @Override
    public Mpa update(Mpa mpa) {
        jdbcTemplate.update(UPDATE_MPA, mpa.getName(), mpa.getId());
        return getById(mpa.getId());
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update(DELETE_MPA, id);
    }
}
