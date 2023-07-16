package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage extends Storage<Mpa> {

    List<Mpa> get();

    Mpa getById(Integer id);
}
