package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class RatingMpaController {
    private final MpaService mpaService;

    @GetMapping()
    public Collection<Mpa> getMpaList() {
        return mpaService.getMpaList();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Integer id) {
        if (!mpaService.getMpaList().contains(mpaService.getMpaById(id))) {
            log.error("Рейтинга MPA с id {} не существует", id);
            throw new NotFoundException(String.format("Рейтинга MPA с id %s не существует", id));
        }
        return mpaService.getMpaById(id);
    }
}