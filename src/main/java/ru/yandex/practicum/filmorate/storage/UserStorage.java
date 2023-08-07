package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends Storage<User> {

    List<User> getAll();

    User getById(Integer id) throws NotFoundException;

    User create(User user);

    User update(User user);

    void deleteById(Integer id);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);
}