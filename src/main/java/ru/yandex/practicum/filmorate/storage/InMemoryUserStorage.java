package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public User create(User user) {
        validate(user);
        if (users.containsKey(user.getId())) {
            throw new DataAlreadyExistException(String.format("Пользователь с id %s уже существует", user.getId()));
        }
        user.setId(id);
        users.put(id, user);
        id++;
        return user;
    }

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Integer id) {
        return users.get(id);
    }

    @Override
    public User update(User user) {
        validate(user);
        users.put(user.getId(), user);
        return user;
    }

    private static void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public void deleteById(Integer id) {
        users.remove(id);
    }
}