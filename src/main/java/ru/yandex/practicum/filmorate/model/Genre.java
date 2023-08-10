package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Genre {
    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull(message = "Id жанра не может быть пустым")
    @Min(value = 1, message = "Значение id не может быть меньше 1")
    private Integer id;
    private String name;
}
