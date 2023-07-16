package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage extends StorageAdv<User> {
    User create(User user);

    User update(User user);

    List<User> get();

    User getById(Integer id);

    void deleteById(Integer id);

    Set<User> getFriends(Integer id);

    Set<User> getCommonFriends(Integer id, Integer otherId);

    void addingToFriends(Integer id, Integer friendId);

    void deletingFromFriends(Integer id, Integer friendId);
}