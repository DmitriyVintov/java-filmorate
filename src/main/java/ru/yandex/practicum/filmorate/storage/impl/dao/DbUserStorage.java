package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {
    private static final String SELECT_USER = "SELECT id, email, login, name, birthday, f.friend_id as friends " +
            "FROM users as u LEFT JOIN friends as f ON u.id = f.user_id WHERE id = ?";
    private static final String DELETE_FRIENDS = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_USER = "UPDATE users set  email=?, login=?, name=?, birthday=? WHERE id=?";
    private static final String SELECT_ALL_USER = "SELECT id, email, login, name, birthday, f.friend_id as friends " +
            "FROM users as u LEFT JOIN friends as f ON u.id=f.user_id ORDER BY id";
    private static final String DELETE_USER = "delete from users where id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", java.sql.Date.valueOf(user.getBirthday()));
        Number number = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(number.intValue());
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update(UPDATE_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> get() {
        return jdbcTemplate.query(SELECT_ALL_USER, usersRowMapper()).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public User getById(Integer id) {
        return jdbcTemplate.query(SELECT_USER, userRowMapper(), id).stream()
                .findFirst().orElseThrow(() -> new NotFoundException("Пользователя с id " + id + " не существует"));
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public void addingToFriends(Integer id, Integer friendId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("friends");
        Map<String, Integer> params = Map.of("user_id", id, "friend_id", friendId);
        simpleJdbcInsert.execute(params);
    }

    @Override
    public void deletingFromFriends(Integer id, Integer friendId) {
        jdbcTemplate.update(DELETE_FRIENDS, id, friendId);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User(
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
            user.setId(rs.getInt("id"));
            do {
                if (rs.getInt("friends") > 0) {
                    user.getFriends().add(rs.getInt("friends"));
                }
            } while (rs.next());

            return user;
        };
    }

    private RowMapper<List<User>> usersRowMapper() {
        return (rs, rowNum) -> {
            List<User> users = new ArrayList<>();
            User user = new User(
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
            user.setId(rs.getInt("id"));
            if (rs.getInt("friends") > 0) {
                user.getFriends().add(rs.getInt("friends"));
            }
            while (rs.next()) {
                if (user.getId() != rs.getInt("id")) {
                    users.add(user);
                    user = new User(
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getDate("birthday").toLocalDate()
                    );
                    user.setId(rs.getInt("id"));

                }
                if (rs.getInt("friends") > 0) {
                    user.getFriends().add(rs.getInt("friends"));
                }
            }
            users.add(user);

            return users;
        };
    }
}
