package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Mpa {
    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull(message = "Id рейтинга MPA не может быть пустым")
    @Min(value = 1, message = "Значение id не может быть меньше 1")
    private int id;
    private String name;
}