package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.List;

public interface UserEventStorage {
    List<UserEvent> getAll(Integer id);

    void create(UserEvent userEvent);
}
