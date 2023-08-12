package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @GetMapping()
    public Collection<Mpa> getMpaList() {
        log.info("Поступил запрос на получение всех рейтингов MPA");
        return mpaService.getMpaList();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на получение рейтинга MPA с id %s", id));
        return mpaService.getMpaById(id);
    }
}