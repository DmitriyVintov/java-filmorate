package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends StorageAdv<User> {
    User create(User user);

    User update(User user);

    List<User> get();

    User getById(Integer id);

    void deleteById(Integer id);


    void addingToFriends(Integer id, Integer friendId);

    void deletingFromFriends(Integer id, Integer friendId);
}