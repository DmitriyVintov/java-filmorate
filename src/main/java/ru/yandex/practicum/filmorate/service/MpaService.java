package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final Storage<Mpa> mpaStorage;

    public MpaService(Storage<Mpa> mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getMpaList() {
        log.info("Все рейтинги MPA: {}", mpaStorage.getAll());
        return mpaStorage.getAll();
    }

    public Mpa getMpaById(Integer id) {
        log.info("Получение рейтинга MPA с id {}: {}", id, mpaStorage.getById(id));
        return mpaStorage.getById(id);
    }
}
