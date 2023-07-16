package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getMpaList() {
        log.info("Получение всех рейтингов MPA");
        return mpaStorage.get();
    }

    public Mpa getMpaById(Integer id) {
        log.info("Получение рейтинга MPA с id {}", id);
        return mpaStorage.getById(id);
    }
}
