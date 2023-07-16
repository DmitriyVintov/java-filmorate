package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {

    List<T> get();

    T getById(Integer id);
}
