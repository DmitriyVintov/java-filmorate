package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public Set<User> getFriends(Integer id) {
        return getById(id).getFriends().stream()
                .map(this::getById)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        Set<Integer> friendsByOtherId = getById(otherId).getFriends();
        return getById(id).getFriends().stream()
                .filter(friendsByOtherId::contains)
                .map(this::getById)
                .collect(Collectors.toSet());
    }

    @Override
    public void addingToFriends(Integer id, Integer friendId) {
        getById(id).setFriends(friendId);
        getById(friendId).setFriends(id);
    }

    @Override
    public void deletingFromFriends(Integer id, Integer friendId) {
        getById(id).removeFriends(friendId);
        getById(friendId).removeFriends(id);
    }
}