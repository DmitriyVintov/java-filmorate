package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {

    T create(T t);

    List<T> get();

    T update(T t);

    T getById(Integer id);

    void deleteById(Integer id);
}