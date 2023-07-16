package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {
    private static final String SELECT_USER = "SELECT id, name, email, login, birthday, f.friend_id as friends " +
            "FROM users as u LEFT JOIN friends as f ON u.id=f.user_id WHERE id = ?";
    private static final String DELETE_FRIENDS = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_USER = "UPDATE users set name=?, email=?, login=?, birthday=? WHERE id=?";
    private static final String SELECT_ALL_USER = "SELECT id, name, email, login, birthday, f.friend_id as friends " +
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
//        String sql = "update users set email = ?, login = ?, " +
//                "name = ?, birthday = ? where id = ?";
//        int update = jdbcTemplate.update(sql, user.getEmail(),
//                user.getLogin(), user.getName(), user.getBirthday(), user.getId());
//        user.setId(update);
//        return user;
    }

    @Override
    public List<User> get() {
        return jdbcTemplate.query(SELECT_ALL_USER, usersRowMapper()).stream().findFirst().orElse(new ArrayList<>());
//        String sql = "select u.id, u.email, u.login, u.name, u.birthday, uf.friend_id " +
//                "from users u left join friends uf on u.id = uf.user_id";
//        return jdbcTemplate.query(sql, userRowMapper());
    }

    @Override
    public User getById(Integer id) {
        return jdbcTemplate.query(SELECT_USER, userRowMapper(), id).stream()
                .findFirst().orElseThrow(() -> new NotFoundException("Пользователя с id " + id + " не существует"));
//        String sql = "select u.id, u.email, u.login, u.name, u.birthday, uf.friend_id " +
//                "from users u left join friends uf on u.id = uf.user_id where id = ?";
//        User user;
//        try {
//            user = jdbcTemplate.queryForObject(sql, userRowMapper(), id);
//        } catch (DataAccessException e) {
//            return null;
//        }
//        return user;
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public Set<User> getFriends(Integer id) {
        String sql = "select u.id, u.email, u.login, u.name, u.birthday, user_id " +
                "from friends uf left join users u on u.id = uf.friend_id where user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, userRowMapper(), id));
    }

    @Override
    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        String sql = "select u.id as id, u.email, u.login, u.name, u.birthday, user_id " +
                "from friends uf " +
                "join users u on u.id = uf.friend_id where user_id = ? " +
                "intersect " +
                "select u.id, u.email, u.login, u.name, u.birthday, user_id " +
                "from friends uf " +
                "join users u on u.id = uf.friend_id where user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, userRowMapper(), id, otherId));
    }
    ;;
    @Override
    public void addingToFriends(Integer id, Integer friendId) {
//        List<User> users1 = jdbcTemplate.query("select * from users_friends where user_id = ? and friend_id = ?",
//                userRowMapper(), id, friendId);
//        if (users1.size() != 0) {
//            throw new DataAlreadyExistException(String.format("Пользователь с id %s уже находится у вас в друзьях", friendId));
//        }
        jdbcTemplate.update("insert into friends values (?, ?)",
                id, friendId);
    }

    @Override
    public void deletingFromFriends(Integer id, Integer friendId) {
//        String sql = "select * from users_friends where user_id = ? and friend_id = ?";
//        List<User> users = jdbcTemplate.query(sql, userRowMapper());
//        if (users.size() == 0) {
//            throw new NotFoundException(String.format("Пользователя с id %s нет у вас в друзьях", friendId));
//        }
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", id, friendId);
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

//    private RowMapper<User> userRowMapper() {
//        return (rs, rowNum) -> {
//            User user = new User(
//                    rs.getString("email"),
//                    rs.getString("login"),
//                    rs.getString("name"),
//                    rs.getDate("birthday").toLocalDate()
//            );
//            user.setId(rs.getInt("id"));
//            List<Integer> friendsById = getFriendsById(user.getId());
//            for (Integer id : friendsById) {
//                user.setFriends(id);
//            }
//            return user;
//        };
//    }

//    private List<Integer> getFriendsById(Integer id) {
//        String sql = "select friend_id from friends where user_id = ?";
//        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), id);
//    }
}
