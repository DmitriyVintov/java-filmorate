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
    private static final String GET_USER = "SELECT u.user_id, u.email, u.login, u.user_name, u.birthday, f.friend_id " +
            "FROM users as u LEFT JOIN friends as f ON u.user_id = f.user_id WHERE u.user_id = ?";
    private static final String GET_ALL_USERS = "SELECT u.user_id, email, login, user_name, birthday, f.friend_id " +
            "FROM users as u LEFT JOIN friends as f ON u.user_id = f.user_id ORDER BY u.user_id";
    private static final String UPDATE_USER = "UPDATE users set  email = ?, login = ?, user_name=?, birthday=? WHERE user_id=?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String ADD_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (? , ?)";
    private static final String DELETE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, Object> params = Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "user_name", user.getName(),
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
    public List<User> getAll() {
        return jdbcTemplate.query(GET_ALL_USERS, usersListRowMapper()).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public User getById(Integer id) {
        return jdbcTemplate.query(GET_USER, userRowMapper(), id).stream()
                .findFirst().orElseThrow(() -> new NotFoundException("Пользователя с id " + id + " не существует"));
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(ADD_FRIEND, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User(
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("user_name"),
                    rs.getDate("birthday").toLocalDate()
            );
            user.setId(rs.getInt("user_id"));
            do {
                if (rs.getInt("friend_id") > 0) {
                    user.getFriends().add(rs.getInt("friend_id"));
                }
            } while (rs.next());

            return user;
        };
    }

    private RowMapper<List<User>> usersListRowMapper() {
        return (rs, rowNum) -> {
            List<User> users = new ArrayList<>();
            User user = new User(
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("user_name"),
                    rs.getDate("birthday").toLocalDate()
            );
            user.setId(rs.getInt("user_id"));
            if (rs.getInt("friend_id") > 0) {
                user.getFriends().add(rs.getInt("friend_id"));
            }
            while (rs.next()) {
                if (user.getId() != rs.getInt("user_id")) {
                    users.add(user);
                    user = new User(
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("user_name"),
                            rs.getDate("birthday").toLocalDate()
                    );
                    user.setId(rs.getInt("user_id"));

                }
                if (rs.getInt("friend_id") > 0) {
                    user.getFriends().add(rs.getInt("friend_id"));
                }
            }
            users.add(user);

            return users;
        };
    }
}
